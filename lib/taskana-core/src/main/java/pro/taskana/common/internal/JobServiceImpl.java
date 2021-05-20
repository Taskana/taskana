package pro.taskana.common.internal;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;

/** Controls all job activities. */
public class JobServiceImpl implements JobService {

  public static final Integer JOB_DEFAULT_PRIORITY = 50;
  public static final long DEFAULT_LOCK_EXPIRATION_PERIOD = 60000;

  private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);
  private JobMapper jobMapper;
  private InternalTaskanaEngine taskanaEngineImpl;

  public JobServiceImpl(InternalTaskanaEngine taskanaEngine, JobMapper jobMapper) {
    this.taskanaEngineImpl = taskanaEngine;
    this.jobMapper = jobMapper;
  }

  @Override
  public ScheduledJob createJob(ScheduledJob job) {
    try {
      taskanaEngineImpl.openConnection();
      job = initializeJobDefault(job);
      Integer jobId = jobMapper.insertJob(job);
      job.setJobId(jobId);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created job {}", job);
      }
    } finally {
      taskanaEngineImpl.returnConnection();
    }
    return job;
  }

  public void deleteJobs(Type jobType) {
    try {
      taskanaEngineImpl.openConnection();
      jobMapper.deleteMultiple(jobType);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Deleted jobs of type: {}", jobType);
      }
    } finally {
      taskanaEngineImpl.returnConnection();
    }
  }

  public ScheduledJob lockJob(ScheduledJob job, String owner) {
    try {
      taskanaEngineImpl.openConnection();
      job.setLockedBy(owner);
      job.setLockExpires(Instant.now().plusMillis(DEFAULT_LOCK_EXPIRATION_PERIOD));
      job.setRetryCount(job.getRetryCount() - 1);
      jobMapper.update(job);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Job {} locked. Remaining retries: {}", job.getJobId(), job.getRetryCount());
      }
    } finally {
      taskanaEngineImpl.returnConnection();
    }
    return job;
  }

  public List<ScheduledJob> findJobsToRun() {
    List<ScheduledJob> availableJobs;
    try {
      taskanaEngineImpl.openConnection();
      availableJobs = jobMapper.findJobsToRun(Instant.now());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Found available jobs: {}", availableJobs);
      }
    } finally {
      taskanaEngineImpl.returnConnection();
    }
    return availableJobs;
  }

  public void deleteJob(ScheduledJob job) {
    try {
      taskanaEngineImpl.openConnection();
      jobMapper.delete(job);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Deleted job: {}", job);
      }
    } finally {
      taskanaEngineImpl.returnConnection();
    }
  }

  private ScheduledJob initializeJobDefault(ScheduledJob job) {
    job.setCreated(Instant.now());
    job.setState(ScheduledJob.State.READY);
    job.setPriority(JOB_DEFAULT_PRIORITY);
    if (job.getDue() == null) {
      job.setDue(Instant.now());
    }
    job.setRetryCount(taskanaEngineImpl.getEngine().getConfiguration().getMaxNumberOfJobRetries());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Job after initialization: {}", job);
    }
    return job;
  }
}
