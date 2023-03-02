package pro.taskana.common.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.JobService;
import pro.taskana.common.api.ScheduledJob;

/** Controls all job activities. */
@Slf4j
@AllArgsConstructor
public class JobServiceImpl implements JobService {

  public static final int JOB_DEFAULT_PRIORITY = 50;
  private static final Duration JOB_DEFAULT_LOCK_EXPIRATION_PERIOD = Duration.ofSeconds(60);

  private final JobMapper jobMapper;
  private final InternalTaskanaEngine taskanaEngineImpl;

  @Override
  public ScheduledJob createJob(ScheduledJob job) {
    initializeDefaultJobProperties(job);
    Integer id = taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.insertJob(job));
    job.setJobId(id);
    log.debug("Created job {}", job);
    return job;
  }

  public void deleteJobs(String jobType) {
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.deleteMultiple(jobType));
    log.debug("Deleted jobs of type: {}", jobType);
  }

  public ScheduledJob lockJob(ScheduledJob job, String owner) {
    job.setLockedBy(owner);
    job.setLockExpires(Instant.now().plus(JOB_DEFAULT_LOCK_EXPIRATION_PERIOD));
    job.setRetryCount(job.getRetryCount() - 1);
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.update(job));
    log.debug("Job {} locked. Remaining retries: {}", job.getJobId(), job.getRetryCount());
    return job;
  }

  public List<ScheduledJob> findJobsToRun() {
    List<ScheduledJob> availableJobs =
        taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.findJobsToRun(Instant.now()));
    log.debug("Found available jobs: {}", availableJobs);
    return availableJobs;
  }

  public void deleteJob(ScheduledJob job) {
    taskanaEngineImpl.executeInDatabaseConnection(() -> jobMapper.delete(job));
    log.debug("Deleted job: {}", job);
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
    log.debug("Job after initialization: {}", job);
  }
}
