package pro.taskana.task.internal.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.task.api.models.TaskSummary;

/** Job to cleanup completed tasks after a period of time. */
public class TaskCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

  private static final SortDirection ASCENDING = SortDirection.ASCENDING;

  // Parameter
  private final Duration minimumAge;
  private final int batchSize;
  private final boolean allCompletedSameParentBusiness;

  public TaskCleanupJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider<Object> txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob);
    minimumAge = taskanaEngine.getConfiguration().getCleanupJobMinimumAge();
    batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
    allCompletedSameParentBusiness =
        taskanaEngine.getConfiguration().isTaskCleanupJobAllCompletedSameParentBusiness();
  }

  @Override
  public void run() throws TaskanaException {
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
      throw new TaskanaException("Error while processing TaskCleanupJob.", e);
    } finally {
      scheduleNextCleanupJob();
    }
  }

  /**
   * Initializes the TaskCleanupJob schedule. <br>
   * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the TASKANA engine.
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine) {
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.deleteJobs(Type.TASKCLEANUPJOB);
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.scheduleNextCleanupJob();
  }

  private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {
    LOGGER.debug("entry to getTasksCompletedBefore(untilDate = {})", untilDate);

    List<TaskSummary> tasksToDelete =
        taskanaEngineImpl
            .getTaskService()
            .createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .orderByBusinessProcessId(ASCENDING)
            .list();

    if (allCompletedSameParentBusiness) {
      Map<String, Long> numberParentTasksShouldHave = new HashMap<>();
      Map<String, Long> countParentTask = new HashMap<>();

      tasksToDelete.forEach(
          task -> {
            if (!numberParentTasksShouldHave.containsKey(task.getParentBusinessProcessId())) {
              numberParentTasksShouldHave.put(
                  task.getParentBusinessProcessId(),
                  taskanaEngineImpl
                      .getTaskService()
                      .createTaskQuery()
                      .parentBusinessProcessIdIn(task.getParentBusinessProcessId())
                      .count());
            }
            countParentTask.merge(task.getParentBusinessProcessId(), 1L, Long::sum);
          });

      List<String> taskIdsNotAllCompletedSameParentBusiness =
          numberParentTasksShouldHave.entrySet().stream()
              .filter(entry -> entry.getKey() != null)
              .filter(entry -> !entry.getKey().isEmpty())
              .filter(entry -> !entry.getValue().equals(countParentTask.get(entry.getKey())))
              .map(Map.Entry::getKey)
              .collect(Collectors.toList());

      tasksToDelete =
          tasksToDelete.stream()
              .filter(
                  taskSummary ->
                      !taskIdsNotAllCompletedSameParentBusiness.contains(
                          taskSummary.getParentBusinessProcessId()))
              .collect(Collectors.toList());
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("exit from getTasksCompletedBefore(), returning {}", tasksToDelete);
    }

    return tasksToDelete;
  }

  private int deleteTasksTransactionally(List<TaskSummary> tasksToBeDeleted) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to deleteTasksTransactionally(tasksToBeDeleted = {})", tasksToBeDeleted);
    }

    int deletedTaskCount = 0;
    if (txProvider != null) {
      int count =
          (Integer)
              txProvider.executeInTransaction(
                  () -> {
                    try {
                      return deleteTasks(tasksToBeDeleted);
                    } catch (Exception e) {
                      LOGGER.warn("Could not delete tasks.", e);
                      return 0;
                    }
                  });
      LOGGER.debug("exit from deleteTasksTransactionally(), returning {}", count);
      return count;
    } else {
      try {
        deletedTaskCount = deleteTasks(tasksToBeDeleted);
      } catch (Exception e) {
        LOGGER.warn("Could not delete tasks.", e);
      }
    }
    LOGGER.debug("exit from deleteTasksTransactionally(), returning {}", deletedTaskCount);
    return deletedTaskCount;
  }

  private int deleteTasks(List<TaskSummary> tasksToBeDeleted)
      throws InvalidArgumentException, NotAuthorizedException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to deleteTasks(tasksToBeDeleted = {})", tasksToBeDeleted);
    }

    List<String> tasksIdsToBeDeleted =
        tasksToBeDeleted.stream().map(TaskSummary::getId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngineImpl.getTaskService().deleteTasks(tasksIdsToBeDeleted);
    LOGGER.debug("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
    for (String failedId : results.getFailedIds()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn(
            "Task with id {} could not be deleted. Reason: {}",
            LogSanitizer.stripLineBreakingChars(failedId),
            LogSanitizer.stripLineBreakingChars(results.getErrorForId(failedId)));
      }
    }
    LOGGER.debug(
        "exit from deleteTasks(), returning {}",
        tasksIdsToBeDeleted.size() - results.getFailedIds().size());
    return tasksIdsToBeDeleted.size() - results.getFailedIds().size();
  }

  private void scheduleNextCleanupJob() {
    LOGGER.debug("Entry to scheduleNextCleanupJob.");
    ScheduledJob job = new ScheduledJob();
    job.setType(ScheduledJob.Type.TASKCLEANUPJOB);
    job.setDue(getNextDueForCleanupJob());
    taskanaEngineImpl.getJobService().createJob(job);
    LOGGER.debug("Exit from scheduleNextCleanupJob.");
  }
}
