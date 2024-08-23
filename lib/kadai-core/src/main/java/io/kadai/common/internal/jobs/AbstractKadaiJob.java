package io.kadai.common.internal.jobs;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.JobServiceImpl;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;

/** Abstract base for all background jobs of KADAI. */
public abstract class AbstractKadaiJob implements KadaiJob {

  protected final KadaiEngineImpl kadaiEngineImpl;
  protected final KadaiTransactionProvider txProvider;
  protected final ScheduledJob scheduledJob;
  private final boolean async;
  protected Instant firstRun;
  protected Duration runEvery;

  protected AbstractKadaiJob(
      KadaiEngine kadaiEngine,
      KadaiTransactionProvider txProvider,
      ScheduledJob job,
      boolean async) {
    this.kadaiEngineImpl = (KadaiEngineImpl) kadaiEngine;
    this.txProvider = txProvider;
    this.scheduledJob = job;
    this.async = async;
    firstRun = kadaiEngineImpl.getConfiguration().getJobFirstRun();
    runEvery = kadaiEngineImpl.getConfiguration().getJobRunEvery();
  }

  public static KadaiJob createFromScheduledJob(
      KadaiEngine engine, KadaiTransactionProvider txProvider, ScheduledJob job) {

    Class<?> jobClass;
    try {
      jobClass = Thread.currentThread().getContextClassLoader().loadClass(job.getType());
    } catch (ClassNotFoundException e) {
      throw new SystemException(String.format("Can't load class '%s'", job.getType()));
    }

    return initKadaiJob(engine, jobClass, txProvider, job);
  }

  /**
   * Initializes the TaskCleanupJob schedule. <br>
   * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
   *
   * @param kadaiEngine the KADAI engine.
   * @param jobClass the class of the job which should be scheduled
   * @throws SystemException if the jobClass could not be scheduled.
   */
  public static void initializeSchedule(KadaiEngine kadaiEngine, Class<?> jobClass) {
    AbstractKadaiJob job = initKadaiJob(kadaiEngine, jobClass, null, null);
    if (!job.async) {
      throw new SystemException(
          String.format("Job '%s' is not an async job. Please declare it as async", jobClass));
    }
    JobServiceImpl jobService = (JobServiceImpl) kadaiEngine.getJobService();
    jobService.deleteJobs(job.getType());
    job.scheduleNextJob();
  }

  public static Duration getLockExpirationPeriod(KadaiConfiguration kadaiConfiguration) {
    return kadaiConfiguration.getJobLockExpirationPeriod();
  }

  private static AbstractKadaiJob initKadaiJob(
      KadaiEngine kadaiEngine,
      Class<?> jobClass,
      KadaiTransactionProvider txProvider,
      ScheduledJob scheduledJob) {
    if (!AbstractKadaiJob.class.isAssignableFrom(jobClass)) {
      throw new SystemException(
          String.format("Job '%s' is not a subclass of '%s'", jobClass, AbstractKadaiJob.class));
    }
    Constructor<?> constructor;
    try {
      constructor =
          jobClass.getConstructor(
              KadaiEngine.class, KadaiTransactionProvider.class, ScheduledJob.class);
    } catch (NoSuchMethodException e) {
      throw new SystemException(
          String.format(
              "Job '%s' does not have a constructor matching (%s, %s, %s)",
              jobClass, KadaiEngine.class, KadaiTransactionProvider.class, ScheduledJob.class));
    }
    AbstractKadaiJob job;
    try {
      job = (AbstractKadaiJob) constructor.newInstance(kadaiEngine, txProvider, scheduledJob);
    } catch (InvocationTargetException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' could not be invoked",
              KadaiEngine.class, KadaiTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    } catch (InstantiationException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' could not be initialized",
              KadaiEngine.class, KadaiTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    } catch (IllegalAccessException e) {
      throw new SystemException(
          String.format(
              "Required Constructor(%s, %s, %s) of job '%s' is not public",
              KadaiEngine.class, KadaiTransactionProvider.class, ScheduledJob.class, jobClass),
          e);
    }
    return job;
  }

  @Override
  public final void run() throws KadaiException {
    execute();
    if (async) {
      scheduleNextJob();
    }
  }

  public boolean isAsync() {
    return async;
  }

  public Instant getFirstRun() {
    return firstRun;
  }

  public Duration getRunEvery() {
    return runEvery;
  }

  protected abstract String getType();

  protected abstract void execute() throws KadaiException;

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
    kadaiEngineImpl.getJobService().createJob(job);
  }
}
