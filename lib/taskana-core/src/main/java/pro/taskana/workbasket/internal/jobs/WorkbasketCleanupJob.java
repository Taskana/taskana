package pro.taskana.workbasket.internal.jobs;

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
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;

/**
 * Job to cleanup completed workbaskets after a period of time if there are no pending tasks
 * associated to the workbasket.
 */
public class WorkbasketCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketCleanupJob.class);
  private final int batchSize;

  public WorkbasketCleanupJob(TaskanaEngine taskanaEngine, ScheduledJob job) {
    super(taskanaEngine, job, true);
    batchSize = taskanaEngine.getConfiguration().getMaxNumberOfUpdatesPerTransaction();
  }

  @Override
  public void executeJob() throws TaskanaException {
    LOGGER.info("Running job to delete all workbaskets marked for deletion");
    try {
      List<String> workbasketsMarkedForDeletion = getWorkbasketsMarkedForDeletion();
      int totalNumberOfWorkbasketDeleted =
          CollectionUtil.partitionBasedOnSize(workbasketsMarkedForDeletion, batchSize).stream()
              .mapToInt(
                  workbasketsToBeDeleted -> {
                    try {
                      return deleteWorkbaskets(workbasketsToBeDeleted);
                    } catch (Exception e) {
                      return 0;
                    }
                  })
              .sum();
      LOGGER.info(
          "Job ended successfully. {} workbaskets deleted.", totalNumberOfWorkbasketDeleted);
    } catch (Exception e) {
      throw new TaskanaException("Error while processing WorkbasketCleanupJob.", e);
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
    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null);
    jobService.deleteJobs(job.getJobType());
    job.scheduleNextCleanupJob();
  }

  @Override
  protected Type getJobType() {
    return Type.WORKBASKET_CLEANUP_JOB;
  }

  private List<String> getWorkbasketsMarkedForDeletion() {
    return taskanaEngine
        .getWorkbasketService()
        .createWorkbasketQuery()
        .markedForDeletion(true)
        .listValues(WorkbasketQueryColumnName.ID, BaseQuery.SortDirection.ASCENDING);
  }

  private int deleteWorkbaskets(List<String> workbasketsToBeDeleted)
      throws InvalidArgumentException, NotAuthorizedException {

    BulkOperationResults<String, TaskanaException> results =
        taskanaEngine.getWorkbasketService().deleteWorkbaskets(workbasketsToBeDeleted);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "{} workbasket deleted.", workbasketsToBeDeleted.size() - results.getFailedIds().size());
    }
    for (String failedId : results.getFailedIds()) {
      LOGGER.warn(
          "Workbasket with id {} could not be deleted. Reason:",
          failedId,
          results.getErrorForId(failedId));
    }
    return workbasketsToBeDeleted.size() - results.getFailedIds().size();
  }

  @Override
  public String toString() {
    return "WorkbasketCleanupJob [batchSize=" + batchSize + "]";
  }
}
