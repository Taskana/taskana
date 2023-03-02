package pro.taskana.task.internal.jobs;

import static pro.taskana.common.internal.util.CollectionUtil.partitionBasedOnSize;

import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;

/** Job to recalculate the priority of each task that is not in an endstate. */
@Slf4j
public class TaskUpdatePriorityJob extends AbstractTaskanaJob {

  private final int batchSize;
  private final boolean isJobActive;

  public TaskUpdatePriorityJob(TaskanaEngine taskanaEngine) {
    this(taskanaEngine, null, null);
  }

  public TaskUpdatePriorityJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob, true);
    batchSize = taskanaEngine.getConfiguration().getPriorityJobBatchSize();
    isJobActive = taskanaEngine.getConfiguration().isPriorityJobActive();
    runEvery = taskanaEngine.getConfiguration().getPriorityJobRunEvery();
    firstRun = taskanaEngine.getConfiguration().getPriorityJobFirstRun();
  }

  @Override
  public void execute() {
    if (!isJobActive()) {
      log.debug("Job to update task priority is not active.");
      return;
    }
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngineImpl);
    log.info("Running job to calculate all non finished task priorities");
    try {
      partitionBasedOnSize(worker.getAllRelevantTaskIds(), getBatchSize())
          .forEach(worker::executeBatch);
      log.info("Job to update priority of tasks has finished.");
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskUpdatePriorityJob.", e);
    }
  }

  public boolean isJobActive() {
    return isJobActive;
  }

  public int getBatchSize() {
    return batchSize;
  }

  /**
   * Initializes the TaskUpdatePriorityJob schedule. <br>
   * All scheduled jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the TASKANA engine.
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine) {
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    TaskUpdatePriorityJob job = new TaskUpdatePriorityJob(taskanaEngine);
    jobService.deleteJobs(job.getType());
    job.scheduleNextJob();
  }

  @Override
  protected String getType() {
    return TaskUpdatePriorityJob.class.getName();
  }

  @Override
  public String toString() {
    return "TaskUpdatePriorityJob [firstRun="
        + firstRun
        + ", runEvery="
        + runEvery
        + ", taskanaEngineImpl="
        + taskanaEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + ", batchSize="
        + batchSize
        + "]";
  }
}
