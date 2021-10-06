package pro.taskana.common.internal.jobs;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** Abstract base for all background jobs of TASKANA. */
public abstract class AbstractTaskanaJob implements TaskanaJob {

  protected Instant firstRun;
  protected Duration runEvery;
  protected final TaskanaEngineImpl taskanaEngineImpl;
  protected final TaskanaTransactionProvider txProvider;
  protected final ScheduledJob scheduledJob;
  private final boolean async;

  protected AbstractTaskanaJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider txProvider,
      ScheduledJob job,
      boolean async) {
    this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    this.txProvider = txProvider;
    this.scheduledJob = job;
    this.async = async;
    firstRun = taskanaEngineImpl.getConfiguration().getCleanupJobFirstRun();
    runEvery = taskanaEngineImpl.getConfiguration().getCleanupJobRunEvery();
  }

  public static TaskanaJob createFromScheduledJob(
      TaskanaEngine engine, TaskanaTransactionProvider txProvider, ScheduledJob job)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException,
          InvocationTargetException {

    return (TaskanaJob)
        Thread.currentThread()
            .getContextClassLoader()
            .loadClass(job.getType())
            .getConstructors()[0]
            .newInstance(engine, txProvider, job);
  }

  @Override
  public final void run() throws TaskanaException {
    execute();
    if (async) {
      scheduleNextJob();
    }
  }

  protected abstract String getType();

  protected abstract void execute() throws TaskanaException;

  protected Instant getNextDueForJob() {
    Instant nextRun = firstRun;
    if (scheduledJob != null && scheduledJob.getDue() != null) {
      nextRun = scheduledJob.getDue();
    }

    while (nextRun.isBefore(Instant.now())) {
      nextRun = nextRun.plus(runEvery);
    }

    return nextRun;
  }

  protected void scheduleNextJob() {
    ScheduledJob job = new ScheduledJob();
    job.setType(getType());
    job.setDue(getNextDueForJob());
    taskanaEngineImpl.getJobService().createJob(job);
  }
}
