package pro.taskana.workbasket.internal.jobs;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.CollectionUtil;
import pro.taskana.workbasket.api.WorkbasketQueryColumnName;

/**
 * Job to cleanup completed workbaskets after a period of time if there are no pending tasks
 * associated to the workbasket.
 */
public class WorkbasketCleanupJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketCleanupJob.class);

  private final int batchSize;

  public WorkbasketCleanupJob(
      TaskanaEngine taskanaEngine, TaskanaTransactionProvider txProvider, ScheduledJob job) {
    super(taskanaEngine, txProvider, job, true);
    batchSize = taskanaEngine.getConfiguration().getJobBatchSize();
  }

  @Override
  public void execute() throws TaskanaException {
    LOGGER.info("Running job to delete all workbaskets marked for deletion");
    try {
      List<String> workbasketsMarkedForDeletion = getWorkbasketsMarkedForDeletion();
      int totalNumberOfWorkbasketDeleted =
          CollectionUtil.partitionBasedOnSize(workbasketsMarkedForDeletion, batchSize).stream()
              .mapToInt(this::deleteWorkbasketsTransactionally)
              .sum();
      LOGGER.info(
          "Job ended successfully. {} workbaskets deleted.", totalNumberOfWorkbasketDeleted);
    } catch (Exception e) {
      throw new SystemException("Error while processing WorkbasketCleanupJob.", e);
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
    WorkbasketCleanupJob job = new WorkbasketCleanupJob(taskanaEngine, null, null);
    jobService.deleteJobs(job.getType());
    job.scheduleNextJob();
  }

  @Override
  protected String getType() {
    return WorkbasketCleanupJob.class.getName();
  }

  private List<String> getWorkbasketsMarkedForDeletion() {

    return taskanaEngineImpl
        .getWorkbasketService()
        .createWorkbasketQuery()
        .markedForDeletion(true)
        .listValues(WorkbasketQueryColumnName.ID, BaseQuery.SortDirection.ASCENDING);
  }

  private int deleteWorkbasketsTransactionally(List<String> workbasketsToBeDeleted) {
    return TaskanaTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> {
          try {
            return deleteWorkbaskets(workbasketsToBeDeleted);
          } catch (Exception e) {
            LOGGER.warn("Could not delete workbaskets.", e);
            return 0;
          }
        });
  }

  private int deleteWorkbaskets(List<String> workbasketsToBeDeleted)
      throws InvalidArgumentException, MismatchedRoleException {

    BulkOperationResults<String, TaskanaException> results =
        taskanaEngineImpl.getWorkbasketService().deleteWorkbaskets(workbasketsToBeDeleted);
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
}
