package pro.taskana.workbasket.internal.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;

/**
 * Job to cleanup completed workbaskets after a period of time if there are no pending tasks
 * associated to the workbasket.
 */
public class WorkbasketCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketCleanupJob.class);

  // Parameter
  private final Instant firstRun;
  private final Duration runEvery;
  private final int batchSize;

  public WorkbasketCleanupJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider<Object> txProvider,
      ScheduledJob job) {
    super(taskanaEngine, txProvider, job);
    firstRun = taskanaEngine.getConfiguration().getCleanupJobFirstRun();
    runEvery = taskanaEngine.getConfiguration().getCleanupJobRunEvery();
    batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
  }

  @Override
  public void run() throws TaskanaException {
    LOGGER.info("Running job to delete all workbaskets marked for deletion");
    try {
      List<String> workbasketsMarkedForDeletion = getWorkbasketsMarkedForDeletion();
      int totalNumberOfWorkbasketDeleted = 0;
      while (workbasketsMarkedForDeletion.size() > 0) {
        int upperLimit = batchSize;
        if (upperLimit > workbasketsMarkedForDeletion.size()) {
          upperLimit = workbasketsMarkedForDeletion.size();
        }
        totalNumberOfWorkbasketDeleted +=
            deleteWorkbasketsTransactionally(workbasketsMarkedForDeletion.subList(0, upperLimit));
        workbasketsMarkedForDeletion.subList(0, upperLimit).clear();
      }
      LOGGER.info(
          "Job ended successfully. {} workbaskets deleted.", totalNumberOfWorkbasketDeleted);
    } catch (Exception e) {
      throw new TaskanaException("Error while processing WorkbasketCleanupJob.", e);
    } finally {
      scheduleNextCleanupJob();
    }
  }

  /**
   * Initializes the WorkbasketCleanupJob schedule. <br>
   * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the taskana engine
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine) {
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.deleteJobs(Type.WORKBASKETCLEANUPJOB);
    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    job.scheduleNextCleanupJob();
  }

  private List<String> getWorkbasketsMarkedForDeletion() {

    return taskanaEngineImpl
        .getWorkbasketService()
        .createWorkbasketQuery()
        .markedForDeletion(true)
        .listValues(WorkbasketQueryColumnName.ID, BaseQuery.SortDirection.ASCENDING);
  }

  private int deleteWorkbasketsTransactionally(List<String> workbasketsToBeDeleted) {
    int deletedWorkbasketsCount = 0;
    if (txProvider != null) {
      return (Integer)
          txProvider.executeInTransaction(
              () -> {
                try {
                  return deleteWorkbaskets(workbasketsToBeDeleted);
                } catch (Exception e) {
                  LOGGER.warn("Could not delete workbaskets.", e);
                  return 0;
                }
              });
    } else {
      try {
        deletedWorkbasketsCount = deleteWorkbaskets(workbasketsToBeDeleted);
      } catch (Exception e) {
        LOGGER.warn("Could not delete workbaskets.", e);
      }
    }
    return deletedWorkbasketsCount;
  }

  private int deleteWorkbaskets(List<String> workbasketsToBeDeleted)
      throws InvalidArgumentException, NotAuthorizedException {

    BulkOperationResults<String, TaskanaException> results =
        taskanaEngineImpl.getWorkbasketService().deleteWorkbaskets(workbasketsToBeDeleted);
    LOGGER.debug(
        "{} workbasket deleted.", workbasketsToBeDeleted.size() - results.getFailedIds().size());
    for (String failedId : results.getFailedIds()) {
      LOGGER.warn(
          "Workbasket with id {} could not be deleted. Reason:",
          failedId,
          results.getErrorForId(failedId));
    }
    return workbasketsToBeDeleted.size() - results.getFailedIds().size();
  }

  private void scheduleNextCleanupJob() {
    LOGGER.debug("Entry to scheduleNextCleanupJob.");
    ScheduledJob job = new ScheduledJob();
    job.setType(ScheduledJob.Type.WORKBASKETCLEANUPJOB);
    job.setDue(getNextDueForWorkbasketCleanupJob());
    taskanaEngineImpl.getJobService().createJob(job);
    LOGGER.debug("Exit from scheduleNextCleanupJob.");
  }

  private Instant getNextDueForWorkbasketCleanupJob() {
    Instant nextRunAt = firstRun;
    while (nextRunAt.isBefore(Instant.now())) {
      nextRunAt = nextRunAt.plus(runEvery);
    }
    LOGGER.info("Scheduling next run of the WorkbasketCleanupJob for {}", nextRunAt);
    return nextRunAt;
  }
}
