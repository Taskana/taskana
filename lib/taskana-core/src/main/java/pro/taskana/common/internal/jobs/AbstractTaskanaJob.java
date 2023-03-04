package pro.taskana.common.internal.jobs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** Abstract base for all background jobs of TASKANA. */
public abstract class AbstractTaskanaJob implements TaskanaJob {

  protected final TaskanaEngineImpl taskanaEngineImpl;
  protected final TaskanaTransactionProvider txProvider;
  protected final ScheduledJob scheduledJob;
  private final boolean async;
  protected Instant firstRun;
  protected Duration runEvery;

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

  /**
   * Initializes the TaskCleanupJob schedule. <br>
   * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param taskanaEngine the TASKANA engine.
   * @param jobClass the class of the job which should be scheduled
   * @throws SystemException if the jobClass could not be scheduled.
   */
  public static void initializeSchedule(TaskanaEngine taskanaEngine, Class<?> jobClass) {
    if (!AbstractTaskanaJob.class.isAssignableFrom(jobClass)) {
      throw new SystemException(
          String.format("Job '%s' is not a subclass of '%s'", jobClass, AbstractTaskanaJob.class));
    }
    Constructor<?> constructor;
    try {
      constructor =
          jobClass.getConstructor(
              TaskanaEngine.class, TaskanaTransactionProvider.class, ScheduledJob.class);
    } catch (NoSuchMethodException e) {
      throw new SystemException(
          String.format(
              "Job '%s' does not have a constructor matching (%s, %s, %s)",
              jobClass, TaskanaEngine.class, TaskanaTransactionProvider.class, ScheduledJob.class));
    }
    AbstractTaskanaJob job;
    try {
      job = (AbstractTaskanaJob) constructor.newInstance(taskanaEngine, null, null);
    } catch (InvocationTargetException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' could not be invoked",
              TaskanaEngine.class, TaskanaTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    } catch (InstantiationException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' could not be initialized",
              TaskanaEngine.class, TaskanaTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    } catch (IllegalAccessException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' is not public",
              TaskanaEngine.class, TaskanaTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    }
    if (!job.async) {
      throw new SystemException(
          String.format("Job '%s' is not an async job. Please declare it as async", jobClass));
    }
    JobServiceImpl jobService = (JobServiceImpl) taskanaEngine.getJobService();
    jobService.deleteJobs(job.getType());
    job.scheduleNextJob();
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
