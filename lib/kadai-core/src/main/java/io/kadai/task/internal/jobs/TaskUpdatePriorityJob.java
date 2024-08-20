package io.kadai.task.internal.jobs;

import static io.kadai.common.internal.util.CollectionUtil.partitionBasedOnSize;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.task.internal.jobs.helper.TaskUpdatePriorityWorker;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Job to recalculate the priority of each task that is not in an endstate. */
public class TaskUpdatePriorityJob extends AbstractKadaiJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskUpdatePriorityJob.class);

  private final int batchSize;

  public TaskUpdatePriorityJob(KadaiEngine kadaiEngine) {
    this(kadaiEngine, null, null);
  }

  public TaskUpdatePriorityJob(
      KadaiEngine kadaiEngine, KadaiTransactionProvider txProvider, ScheduledJob scheduledJob) {
    super(kadaiEngine, txProvider, scheduledJob, true);
    batchSize = kadaiEngine.getConfiguration().getTaskUpdatePriorityJobBatchSize();
    runEvery = kadaiEngine.getConfiguration().getTaskUpdatePriorityJobRunEvery();
    firstRun = kadaiEngine.getConfiguration().getTaskUpdatePriorityJobFirstRun();
  }

  @Override
  public void execute() {
    TaskUpdatePriorityWorker worker = new TaskUpdatePriorityWorker(kadaiEngineImpl);
    LOGGER.info("Running job to calculate all non finished task priorities");
    try {
      partitionBasedOnSize(worker.getAllRelevantTaskIds(), getBatchSize())
          .forEach(worker::executeBatch);
      LOGGER.info("Job to update priority of tasks has finished.");
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskUpdatePriorityJob.", e);
    }
  }

  public static Duration getLockExpirationPeriod(KadaiConfiguration kadaiConfiguration) {
    return kadaiConfiguration.getTaskUpdatePriorityJobLockExpirationPeriod();
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
        + ", kadaiEngineImpl="
        + kadaiEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + ", batchSize="
        + batchSize
        + "]";
  }
}
