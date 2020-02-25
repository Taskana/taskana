package pro.taskana.common.internal.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.task.api.models.TaskSummary;

/** Job to cleanup completed tasks after a period of time. */
public class TaskCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

  private static BaseQuery.SortDirection asc = BaseQuery.SortDirection.ASCENDING;

  // Parameter
  private Instant firstRun;
  private Duration runEvery;
  private Duration minimumAge;
  private int batchSize;
  private boolean allCompletedSameParentBusiness;

  public TaskCleanupJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider<Object> txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob);
    firstRun = taskanaEngine.getConfiguration().getCleanupJobFirstRun();
    runEvery = taskanaEngine.getConfiguration().getCleanupJobRunEvery();
    minimumAge = taskanaEngine.getConfiguration().getCleanupJobMinimumAge();
    batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
    allCompletedSameParentBusiness =
        taskanaEngine.getConfiguration().isTaskCleanupJobAllCompletedSameParentBusiness();
  }

  @Override
  public void run() throws TaskanaException {
    Instant completedBefore = Instant.now().minus(minimumAge);
    LOGGER.info(
        "Running job to delete all tasks completed before ({})", completedBefore.toString());
    try {
      List<TaskSummary> tasksCompletedBefore = getTasksCompletedBefore(completedBefore);
      int totalNumberOfTasksCompleted = 0;
      while (tasksCompletedBefore.size() > 0) {
        int upperLimit = batchSize;
        if (upperLimit > tasksCompletedBefore.size()) {
          upperLimit = tasksCompletedBefore.size();
        }
        totalNumberOfTasksCompleted +=
            deleteTasksTransactionally(tasksCompletedBefore.subList(0, upperLimit));
        tasksCompletedBefore.subList(0, upperLimit).clear();
      }
      LOGGER.info("Job ended successfully. {} tasks deleted.", totalNumberOfTasksCompleted);
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
    TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null, null);
    job.scheduleNextCleanupJob();
  }

  private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {
    LOGGER.debug("entry to getTasksCompletedBefore(untilDate = {})", untilDate);
    List<TaskSummary> taskList =
        taskanaEngineImpl
            .getTaskService()
            .createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .orderByBusinessProcessId(asc)
            .list();

    if (allCompletedSameParentBusiness) {
      Map<String, Long> numberParentTasksShouldHave = new HashMap<>();
      Map<String, Long> countParentTask = new HashMap<>();
      for (TaskSummary task : taskList) {
        numberParentTasksShouldHave.put(
            task.getParentBusinessProcessId(),
            taskanaEngineImpl
                .getTaskService()
                .createTaskQuery()
                .parentBusinessProcessIdIn(task.getParentBusinessProcessId())
                .count());
        countParentTask.merge(task.getParentBusinessProcessId(), 1L, Long::sum);
      }

      List<String> idsList = new ArrayList<>();
      numberParentTasksShouldHave.forEach(
          (k, v) -> {
            if (v.compareTo(countParentTask.get(k)) == 0) {
              idsList.add(k);
            }
          });

      if (idsList.isEmpty()) {
        LOGGER.debug("exit from getTasksCompletedBefore(), returning {}", new ArrayList<>());
        return new ArrayList<>();
      }

      String[] ids = new String[idsList.size()];
      ids = idsList.toArray(ids);
      taskList =
          taskanaEngineImpl
              .getTaskService()
              .createTaskQuery()
              .parentBusinessProcessIdIn(ids)
              .list();
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "exit from getTasksCompletedBefore(), returning {}", LoggerUtils.listToString(taskList));
    }

    return taskList;
  }

  private int deleteTasksTransactionally(List<TaskSummary> tasksToBeDeleted) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to deleteTasksTransactionally(tasksToBeDeleted = {})",
          LoggerUtils.listToString(tasksToBeDeleted));
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

  private int deleteTasks(List<TaskSummary> tasksToBeDeleted) throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to deleteTasks(tasksToBeDeleted = {})", tasksToBeDeleted);
    }

    List<String> tasksIdsToBeDeleted =
        tasksToBeDeleted.stream().map(TaskSummary::getId).collect(Collectors.toList());
    BulkOperationResults<String, TaskanaException> results =
        taskanaEngineImpl.getTaskService().deleteTasks(tasksIdsToBeDeleted);
    LOGGER.debug("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
    for (String failedId : results.getFailedIds()) {
      LOGGER.warn(
          "Task with id {} could not be deleted. Reason: {}",
          LogSanitizer.stripLineBreakingChars(failedId),
          LogSanitizer.stripLineBreakingChars(results.getErrorForId(failedId)));
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
    job.setDue(getNextDueForTaskCleanupJob());
    taskanaEngineImpl.getJobService().createJob(job);
    LOGGER.debug("Exit from scheduleNextCleanupJob.");
  }

  private Instant getNextDueForTaskCleanupJob() {
    Instant nextRunAt = firstRun;
    while (nextRunAt.isBefore(Instant.now())) {
      nextRunAt = nextRunAt.plus(runEvery);
    }
    LOGGER.info("Scheduling next run of the TaskCleanupJob for {}", nextRunAt);
    return nextRunAt;
  }
}
