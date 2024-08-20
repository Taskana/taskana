package io.kadai.simplehistory.impl.jobs;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.util.CollectionUtil;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryCleanupJob extends AbstractKadaiJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCleanupJob.class);
  private final boolean allCompletedSameParentBusiness =
      kadaiEngineImpl.getConfiguration().isSimpleHistoryCleanupJobAllCompletedSameParentBusiness();
  private final Duration minimumAge =
      kadaiEngineImpl.getConfiguration().getSimpleHistoryCleanupJobMinimumAge();
  private final int batchSize =
      kadaiEngineImpl.getConfiguration().getSimpleHistoryCleanupJobBatchSize();
  private SimpleHistoryServiceImpl simpleHistoryService = null;

  public HistoryCleanupJob(
      KadaiEngine kadaiEngine, KadaiTransactionProvider txProvider, ScheduledJob scheduledJob) {
    super(kadaiEngine, txProvider, scheduledJob, true);
    simpleHistoryService = new SimpleHistoryServiceImpl();
    simpleHistoryService.initialize(kadaiEngine);
  }

  public static Duration getLockExpirationPeriod(KadaiConfiguration kadaiConfiguration) {
    return kadaiConfiguration.getSimpleHistoryCleanupJobLockExpirationPeriod();
  }

  @Override
  public void execute() {
    Instant createdBefore = Instant.now().minus(minimumAge);
    LOGGER.info("Running job to delete all history events created before ({})", createdBefore);
    try {
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
    return KadaiTransactionProvider.executeInTransactionIfPossible(
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
