package pro.taskana.common.internal.jobs;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.TaskanaEngineImpl;

/** Abstract base for all background jobs of TASKANA. */
public abstract class AbstractTaskanaJob implements TaskanaJob {

  protected final Instant firstRun;
  protected final Duration runEvery;
  protected final TaskanaEngineImpl taskanaEngine;
  protected final ScheduledJob scheduledJob;
  private final boolean recurring;

  protected AbstractTaskanaJob(TaskanaEngine taskanaEngine, ScheduledJob job, boolean recurring) {
    this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
    this.scheduledJob = job;
    this.recurring = recurring;
    firstRun = this.taskanaEngine.getConfiguration().getCleanupJobFirstRun();
    runEvery = this.taskanaEngine.getConfiguration().getCleanupJobRunEvery();
  }

  public static TaskanaJob createFromScheduledJob(TaskanaEngine engine, ScheduledJob job)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException,
          InvocationTargetException {
    return (TaskanaJob)
        Thread.currentThread()
            .getContextClassLoader()
            .loadClass(job.getType().getClazz())
            .getConstructors()[0]
            .newInstance(engine, job);
  }

  @Override
  public final void run() throws TaskanaException {
    executeJob();
    if (recurring) {
      scheduleNextCleanupJob();
    }
  }

  protected abstract ScheduledJob.Type getJobType();

  protected abstract void executeJob() throws TaskanaException;

  protected void scheduleNextCleanupJob() {
    ScheduledJob job = new ScheduledJob();
    job.setType(getJobType());
    job.setDue(getNextDueForCleanupJob());
    taskanaEngine.getJobService().createJob(job);
  }

  private Instant getNextDueForCleanupJob() {
    Instant nextRun = firstRun;
    if (scheduledJob != null && scheduledJob.getDue() != null) {
      nextRun = scheduledJob.getDue();
    }

    while (nextRun.isBefore(Instant.now())) {
      nextRun = nextRun.plus(runEvery);
    }

    return nextRun;
  }
}
