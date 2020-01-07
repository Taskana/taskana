package pro.taskana.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.JobServiceImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.transaction.TaskanaTransactionProvider;

/** This is the runner for Tasks jobs. */
public class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
  private TaskanaEngineImpl taskanaEngine;
  private JobServiceImpl jobService;
  private TaskanaTransactionProvider<Object> txProvider;

  public JobRunner(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
    jobService = (JobServiceImpl) taskanaEngine.getJobService();
  }

  public void registerTransactionProvider(TaskanaTransactionProvider<Object> txProvider) {
    this.txProvider = txProvider;
  }

  public void runJobs() {
    LOGGER.info("entry to runJobs()");
    try {
      List<ScheduledJob> jobsToRun = findAndLockJobsToRun();
      for (ScheduledJob scheduledJob : jobsToRun) {
        runJobTransactionally(scheduledJob);
      }
    } catch (Exception e) {
      LOGGER.error("Error occurred while running jobs: ", e);
    } finally {
      LOGGER.info("exit from runJobs().");
    }
  }

  private List<ScheduledJob> findAndLockJobsToRun() {
    List<ScheduledJob> availableJobs = jobService.findJobsToRun();
    List<ScheduledJob> lockedJobs = new ArrayList<ScheduledJob>();
    for (ScheduledJob job : availableJobs) {
      lockedJobs.add(lockJobTransactionally(job));
    }
    return lockedJobs;
  }

  private ScheduledJob lockJobTransactionally(ScheduledJob job) {
    ScheduledJob lockedJob;
    if (txProvider != null) {
      lockedJob = (ScheduledJob) txProvider.executeInTransaction(() -> lockJob(job));
    } else {
      lockedJob = lockJob(job);
    }
    LOGGER.debug("Locked job: {}", lockedJob);
    return lockedJob;
  }

  private ScheduledJob lockJob(ScheduledJob job) {
    String hostAddress = "UNKNOWN_ADDRESS";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      //ignore
    }
    job.setLockedBy(hostAddress + " - " + Thread.currentThread().getName());
    String owner = hostAddress + " - " + Thread.currentThread().getName();
    ScheduledJob lockedJob = jobService.lockJob(job, owner);
    return lockedJob;
  }

  private void runJobTransactionally(ScheduledJob scheduledJob) {
    try {
      if (txProvider != null) {
        txProvider.executeInTransaction(
            () -> {
              runScheduledJob(scheduledJob);
              return null;
            });
      } else {
        runScheduledJob(scheduledJob);
      }
      jobService.deleteJob(scheduledJob);
    } catch (Exception e) {
      LOGGER.error(
          "Processing of job {} failed. Trying to split it up into two pieces...",
          scheduledJob.getJobId(),
          e);
    }
  }

  private void runScheduledJob(ScheduledJob scheduledJob) {
    LOGGER.debug("entry to runScheduledJob(job = {})", scheduledJob);
    try {
      TaskanaJob job =
          AbstractTaskanaJob.createFromScheduledJob(taskanaEngine, txProvider, scheduledJob);
      job.run();
    } catch (Exception e) {
      LOGGER.error("Error running job: {} ", scheduledJob.getType(), e);
      throw new SystemException(
          "When attempting to load class "
              + scheduledJob.getType()
              + " caught Exception "
              + e.getMessage(),
          e);
    }
    LOGGER.debug("exit from runScheduledJob");
  }
}
