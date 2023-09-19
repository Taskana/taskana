package pro.taskana.common.internal;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.exceptions.SystemException;

/** Controls all job activities. */
public class JobServiceImpl implements JobService {

  public static final int JOB_DEFAULT_PRIORITY = 50;

  private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);
  private final JobMapper jobMapper;
  private final InternalTaskanaEngine taskanaEngineImpl;

  public JobServiceImpl(InternalTaskanaEngine taskanaEngine, JobMapper jobMapper) {
    this.taskanaEngineImpl = taskanaEngine;
    this.jobMapper = jobMapper;
  }

  @Override
  public ScheduledJob createJob(ScheduledJob job) {
    initializeDefaultJobProperties(job);
    Integer id = taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.insertJob(job));
    job.setJobId(id);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Created job {}", job);
    }
    return job;
  }

  public void deleteJobs(String jobType) {
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.deleteMultiple(jobType));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Deleted jobs of type: {}", jobType);
    }
  }

  public ScheduledJob lockJob(ScheduledJob job, String owner) {
    job.setLockedBy(owner);
    Class<?> jobClass = null;
    try {
      jobClass = Thread.currentThread().getContextClassLoader().loadClass(job.getType());
      job.setLockExpires(
          Instant.now()
              .plus(
                  (Duration)
                      jobClass
                          .getMethod("getLockExpirationPeriod", TaskanaConfiguration.class)
                          .invoke(null, taskanaEngineImpl.getEngine().getConfiguration())));
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new SystemException(
          String.format(
              "Job '%s' does not have a method matching ('getLockExpirationPeriod', %s",
              jobClass, TaskanaConfiguration.class));
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new SystemException(
          String.format(
              "Caught Exception while invoking method 'getLockExpirationPeriod' by reflection"));
    }

    job.setRetryCount(job.getRetryCount() - 1);
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.update(job));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Job {} locked. Remaining retries: {}", job.getJobId(), job.getRetryCount());
    }
    return job;
  }

  public List<ScheduledJob> findJobsToRun() {
    List<ScheduledJob> availableJobs =
        taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.findJobsToRun(Instant.now()));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Found available jobs: {}", availableJobs);
    }
    return availableJobs;
  }

  public void deleteJob(ScheduledJob job) {
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.delete(job));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Deleted job: {}", job);
    }
  }

  private void initializeDefaultJobProperties(ScheduledJob job) {
    Instant now = Instant.now();
    job.setCreated(now);
    job.setState(ScheduledJob.State.READY);
    job.setPriority(JOB_DEFAULT_PRIORITY);
    if (job.getDue() == null) {
      job.setDue(now);
    }
    job.setRetryCount(taskanaEngineImpl.getEngine().getConfiguration().getMaxNumberOfJobRetries());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Job after initialization: {}", job);
    }
  }
}
