package pro.taskana.task.internal.jobs;

import static pro.taskana.common.internal.util.CollectionUtil.partitionBasedOnSize;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.jobs.helper.TaskUpdatePriorityWorker;

/** Job to recalculate the priority of each task that is not in an endstate. */
public class TaskUpdatePriorityJob extends AbstractTaskanaJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskUpdatePriorityJob.class);

  private final int batchSize;

  public TaskUpdatePriorityJob(TaskanaEngine taskanaEngine) {
    this(taskanaEngine, null, null);
  }

  public TaskUpdatePriorityJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    super(taskanaEngine, txProvider, scheduledJob, true);
    batchSize = taskanaEngine.getConfiguration().getTaskUpdatePriorityJobBatchSize();
    runEvery = taskanaEngine.getConfiguration().getTaskUpdatePriorityJobRunEvery();
    firstRun = taskanaEngine.getConfiguration().getTaskUpdatePriorityJobFirstRun();
  }

  @Override
  public void execute() {
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(taskanaEngineImpl);
    LOGGER.info("Running job to calculate all non finished task priorities");
    try {
      partitionBasedOnSize(worker.getAllRelevantTaskIds(), getBatchSize())
          .forEach(worker::executeBatch);
      LOGGER.info("Job to update priority of tasks has finished.");
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskUpdatePriorityJob.", e);
    }
  }

  public static Duration getLockExpirationPeriod(TaskanaConfiguration taskanaConfiguration) {
    return taskanaConfiguration.getTaskUpdatePriorityJobLockExpirationPeriod();
  }

  public int getBatchSize() {
    return batchSize;
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
