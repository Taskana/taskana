package pro.taskana.common.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;

/** Controls all job activities. */
public class JobServiceImpl implements JobService {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

  private static final Integer JOB_DEFAULT_PRIORITY = 50;
  private static final Duration JOB_DEFAULT_LOCK_EXPIRATION_PERIOD = Duration.ofSeconds(60);

  private final JobMapper jobMapper;
  private final InternalTaskanaEngine taskanaEngine;

  public JobServiceImpl(InternalTaskanaEngine taskanaEngine, JobMapper jobMapper) {
    this.taskanaEngine = taskanaEngine;
    this.jobMapper = jobMapper;
  }

  @Override
  public ScheduledJob createJob(ScheduledJob job) {
    initializeDefaultJobProperties(job);
    Integer jobId = taskanaEngine.openAndReturnConnection(() -> jobMapper.insertJob(job));
    job.setJobId(jobId);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Created job {}", job);
    }
    return job;
  }

  public void deleteJobs(Type jobType) {
    taskanaEngine.openAndReturnConnection(() -> jobMapper.deleteMultiple(jobType));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Deleted jobs of type: {}", jobType);
    }
  }

  public ScheduledJob lockJob(ScheduledJob job, String owner) {
    job.setLockedBy(owner);
    job.setLockExpires(Instant.now().plus(JOB_DEFAULT_LOCK_EXPIRATION_PERIOD));
    job.setRetryCount(job.getRetryCount() - 1);
    taskanaEngine.openAndReturnConnection(() -> jobMapper.update(job));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Job {} locked. Remaining retries: {}", job.getJobId(), job.getRetryCount());
    }
    return job;
  }

  public List<ScheduledJob> findJobsToRun() {
    List<ScheduledJob> availableJobs =
        taskanaEngine.openAndReturnConnection(() -> jobMapper.findJobsToRun(Instant.now()));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Found available jobs: {}", availableJobs);
    }
    return availableJobs;
  }

  public void deleteJob(ScheduledJob job) {
    taskanaEngine.openAndReturnConnection(() -> jobMapper.delete(job));
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
    job.setRetryCount(taskanaEngine.getEngine().getConfiguration().getMaxNumberOfJobRetries());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Job after initialization: {}", job);
    }
  }
}
