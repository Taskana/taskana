package io.kadai.task.internal.jobs;

import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.common.internal.util.CollectionUtil;
import io.kadai.common.internal.util.LogSanitizer;
import io.kadai.task.api.models.TaskSummary;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Job to cleanup completed tasks after a period of time. */
public class TaskCleanupJob extends AbstractKadaiJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

  private final Duration minimumAge;
  private final int batchSize;
  private final boolean allCompletedSameParentBusiness;

  public TaskCleanupJob(
      KadaiEngine kadaiEngine, KadaiTransactionProvider txProvider, ScheduledJob scheduledJob) {
    super(kadaiEngine, txProvider, scheduledJob, true);
    minimumAge = kadaiEngine.getConfiguration().getTaskCleanupJobMinimumAge();
    batchSize = kadaiEngine.getConfiguration().getJobBatchSize();
    allCompletedSameParentBusiness =
        kadaiEngine.getConfiguration().isTaskCleanupJobAllCompletedSameParentBusiness();
  }

  @Override
  public void execute() {
    Instant completedBefore = Instant.now().minus(minimumAge);
    LOGGER.info("Running job to delete all tasks completed before ({})", completedBefore);
    try {
      List<TaskSummary> tasksCompletedBefore = getTasksCompletedBefore(completedBefore);

      int totalNumberOfTasksDeleted =
          CollectionUtil.partitionBasedOnSize(tasksCompletedBefore, batchSize).stream()
              .mapToInt(this::deleteTasksTransactionally)
              .sum();

      LOGGER.info("Job ended successfully. {} tasks deleted.", totalNumberOfTasksDeleted);
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskCleanupJob.", e);
    }
  }

  public static Duration getLockExpirationPeriod(KadaiConfiguration kadaiConfiguration) {
    return kadaiConfiguration.getTaskCleanupJobLockExpirationPeriod();
  }

  @Override
  protected String getType() {
    return TaskCleanupJob.class.getName();
  }

  private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {

    List<TaskSummary> tasksToDelete =
        kadaiEngineImpl
            .getTaskService()
            .createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .orderByBusinessProcessId(SortDirection.ASCENDING)
            .list();

    if (allCompletedSameParentBusiness) {
      Map<String, Long> numberParentTasksShouldHave = new HashMap<>();
      Map<String, Long> countParentTask = new HashMap<>();

      tasksToDelete.forEach(
          task -> {
            if (!numberParentTasksShouldHave.containsKey(task.getParentBusinessProcessId())) {
              numberParentTasksShouldHave.put(
                  task.getParentBusinessProcessId(),
                  kadaiEngineImpl
                      .getTaskService()
                      .createTaskQuery()
                      .parentBusinessProcessIdIn(task.getParentBusinessProcessId())
                      .count());
            }
            countParentTask.merge(task.getParentBusinessProcessId(), 1L, Long::sum);
          });

      List<String> taskIdsNotAllCompletedSameParentBusiness =
          numberParentTasksShouldHave.entrySet().stream()
              .filter(entry -> nonNull(entry.getKey()))
              .filter(not(entry -> entry.getKey().isEmpty()))
              .filter(not(entry -> entry.getValue().equals(countParentTask.get(entry.getKey()))))
              .map(Map.Entry::getKey)
              .toList();

      tasksToDelete =
          tasksToDelete.stream()
              .filter(
                  taskSummary ->
                      !taskIdsNotAllCompletedSameParentBusiness.contains(
                          taskSummary.getParentBusinessProcessId()))
              .toList();
    }

    return tasksToDelete;
  }

  private int deleteTasksTransactionally(List<TaskSummary> tasksToBeDeleted) {
    return KadaiTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> {
          try {
            return deleteTasks(tasksToBeDeleted);
          } catch (Exception ex) {
            LOGGER.warn("Could not delete tasks.", ex);
            return 0;
          }
        });
  }

  private int deleteTasks(List<TaskSummary> tasksToBeDeleted)
      throws InvalidArgumentException, NotAuthorizedException {

    List<String> tasksIdsToBeDeleted = tasksToBeDeleted.stream().map(TaskSummary::getId).toList();
    BulkOperationResults<String, KadaiException> results =
        kadaiEngineImpl.getTaskService().deleteTasks(tasksIdsToBeDeleted);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
    }
    for (String failedId : results.getFailedIds()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn(
            "Task with id {} could not be deleted. Reason: {}",
            LogSanitizer.stripLineBreakingChars(failedId),
            LogSanitizer.stripLineBreakingChars(results.getErrorForId(failedId)));
      }
    }
    return tasksIdsToBeDeleted.size() - results.getFailedIds().size();
  }

  @Override
  public String toString() {
    return "TaskCleanupJob [firstRun="
        + firstRun
        + ", runEvery="
        + runEvery
        + ", kadaiEngineImpl="
        + kadaiEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + ", minimumAge="
        + minimumAge
        + ", batchSize="
        + batchSize
        + ", allCompletedSameParentBusiness="
        + allCompletedSameParentBusiness
        + "]";
  }
}
