package pro.taskana.simplehistory.impl.jobs;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;

public class HistoryCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCleanupJob.class);

  private final TaskanaHistoryEngineImpl taskanaHistoryEngine =
      TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineImpl);

  private final boolean allCompletedSameParentBusiness =
      taskanaEngineImpl
          .getConfiguration()
          .isSimpleHistoryCleanupJobAllCompletedSameParentBusiness();

  private final Duration minimumAge =
      taskanaEngineImpl.getConfiguration().getSimpleHistoryCleanupJobMinimumAge();
  private final int batchSize =
      taskanaEngineImpl.getConfiguration().getSimpleHistoryCleanupJobBatchSize();

  public HistoryCleanupJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob, true);
  }

  public static Duration getLockExpirationPeriod(TaskanaConfiguration taskanaConfiguration) {
    return taskanaConfiguration.getSimpleHistoryCleanupJobLockExpirationPeriod();
  }

  @Override
  public void execute() {
    Instant createdBefore = Instant.now().minus(minimumAge);
    LOGGER.info("Running job to delete all history events created before ({})", createdBefore);
    try {
      SimpleHistoryServiceImpl simpleHistoryService =
          (SimpleHistoryServiceImpl) taskanaHistoryEngine.getTaskanaHistoryService();

      List<TaskHistoryEvent> historyEventCandidatesToClean =
          simpleHistoryService
              .createTaskHistoryQuery()
              .createdWithin(new TimeInterval(null, createdBefore))
              .eventTypeIn(
                  TaskHistoryEventType.COMPLETED.getName(),
                  TaskHistoryEventType.CANCELLED.getName(),
                  TaskHistoryEventType.TERMINATED.getName(),
                  TaskHistoryEventType.DELETED.getName())
              .list();

      Set<String> taskIdsToDeleteHistoryEventsFor;
      if (allCompletedSameParentBusiness) {
        taskIdsToDeleteHistoryEventsFor =
            historyEventCandidatesToClean.stream()
                .filter(
                    event ->
                        event.getParentBusinessProcessId() == null
                            || event.getParentBusinessProcessId().isEmpty())
                .map(TaskHistoryEvent::getTaskId)
                .collect(Collectors.toSet());
        historyEventCandidatesToClean.removeIf(
            event -> taskIdsToDeleteHistoryEventsFor.contains(event.getTaskId()));

        if (!historyEventCandidatesToClean.isEmpty()) {
          String[] parentBusinessProcessIds =
              historyEventCandidatesToClean.stream()
                  .map(TaskHistoryEvent::getParentBusinessProcessId)
                  .distinct()
                  .toArray(String[]::new);

          historyEventCandidatesToClean.addAll(
              simpleHistoryService
                  .createTaskHistoryQuery()
                  .parentBusinessProcessIdIn(parentBusinessProcessIds)
                  .eventTypeIn(TaskHistoryEventType.CREATED.getName())
                  .list());

          taskIdsToDeleteHistoryEventsFor.addAll(
              filterSameParentBusinessHistoryEventsQualifiedToClean(historyEventCandidatesToClean));
        }
      } else {
        taskIdsToDeleteHistoryEventsFor =
            historyEventCandidatesToClean.stream()
                .map(TaskHistoryEvent::getTaskId)
                .collect(Collectors.toSet());
      }

      int totalNumberOfHistoryEventsDeleted =
          CollectionUtil.partitionBasedOnSize(taskIdsToDeleteHistoryEventsFor, batchSize).stream()
              .mapToInt(this::deleteHistoryEventsTransactionally)
              .sum();

      LOGGER.info(
          "Job ended successfully. {} history events deleted.", totalNumberOfHistoryEventsDeleted);
    } catch (Exception e) {
      throw new SystemException("Error while processing HistoryCleanupJob.", e);
    }
  }

  @Override
  protected String getType() {
    return HistoryCleanupJob.class.getName();
  }

  private List<String> filterSameParentBusinessHistoryEventsQualifiedToClean(
      List<TaskHistoryEvent> historyEventCandidatesToClean) {

    Map<String, Map<String, List<String>>> taskHistoryIdsByEventTypeByParentBusinessProcessId =
        historyEventCandidatesToClean.stream()
            .collect(
                groupingBy(
                    TaskHistoryEvent::getParentBusinessProcessId,
                    groupingBy(
                        TaskHistoryEvent::getEventType,
                        mapping(TaskHistoryEvent::getTaskId, toList()))));

    List<String> taskIdsToDeleteHistoryEventsFor = new ArrayList<>();
    String createdKey = TaskHistoryEventType.CREATED.getName();

    taskHistoryIdsByEventTypeByParentBusinessProcessId.forEach(
        (parentBusinessProcessId, taskHistoryIdsByEventType) -> {
          if (!taskHistoryIdsByEventType.containsKey(createdKey)) {
            LOGGER.error(
                "Issue during history cleanup tasks with enabled parent business process. "
                    + "No events for parent business process {} with type {} found."
                    + "Please clean up those history events manually",
                parentBusinessProcessId,
                createdKey);
          } else if (taskHistoryIdsByEventType.get(createdKey).size()
              == taskHistoryIdsByEventType.entrySet().stream()
                  .filter(not(entry -> entry.getKey().equals(createdKey)))
                  .mapToInt(stringListEntry -> stringListEntry.getValue().size())
                  .sum()) {
            taskIdsToDeleteHistoryEventsFor.addAll(taskHistoryIdsByEventType.get(createdKey));
          }
        });

    return taskIdsToDeleteHistoryEventsFor;
  }

  private int deleteHistoryEventsTransactionally(List<String> taskIdsToDeleteHistoryEventsFor) {
    return TaskanaTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> {
          try {
            return deleteEvents(taskIdsToDeleteHistoryEventsFor);
          } catch (Exception e) {
            LOGGER.warn("Could not delete history events.", e);
            return 0;
          }
        });
  }

  private int deleteEvents(List<String> taskIdsToDeleteHistoryEventsFor)
      throws InvalidArgumentException, NotAuthorizedException {
    SimpleHistoryServiceImpl simpleHistoryService =
        (SimpleHistoryServiceImpl) taskanaHistoryEngine.getTaskanaHistoryService();

    int deletedTasksCount =
        (int)
            simpleHistoryService
                .createTaskHistoryQuery()
                .taskIdIn(taskIdsToDeleteHistoryEventsFor.toArray(new String[0]))
                .count();

    simpleHistoryService.deleteHistoryEventsByTaskIds(taskIdsToDeleteHistoryEventsFor);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{} events deleted.", deletedTasksCount);
    }

    return deletedTasksCount;
  }
}
