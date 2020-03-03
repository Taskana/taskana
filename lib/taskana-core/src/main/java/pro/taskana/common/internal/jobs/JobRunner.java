package pro.taskana.common.internal.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.security.UserPrincipal;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.TaskServiceImpl;

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
      // ignore
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

    if (taskanaEngine.isUserInRole(TaskanaRole.ADMIN)) {
      // we run already as admin
      runScheduledJobImpl(scheduledJob);
    } else {
      // we must establish admin context
      try {
        Subject.doAs(
            getAdminSubject(),
            new PrivilegedExceptionAction<Object>() {
              @Override
              public Object run() throws Exception {
                try {
                  runScheduledJobImpl(scheduledJob);
                } catch (Exception e) {
                  throw new SystemException(
                      String.format("could not run Job %s.", scheduledJob), e);
                }
                return null;
              }
            });
      } catch (PrivilegedActionException e) {
        LOGGER.warn("Attempt to run job {} failed.", scheduledJob, e);
      }
    }
    LOGGER.debug("exit from runScheduledJob");
  }

  private void runScheduledJobImpl(ScheduledJob scheduledJob) {
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
  }

  private Subject getAdminSubject() {
    Subject subject = new Subject();
    List<Principal> principalList = new ArrayList<>();
    try {
      principalList.add(
          new UserPrincipal(
              taskanaEngine
                  .getConfiguration()
                  .getRoleMap()
                  .get(TaskanaRole.ADMIN)
                  .iterator()
                  .next()));
    } catch (Exception t) {
      LOGGER.warn("Could not determine a configured admin user.", t);
    }
    subject.getPrincipals().addAll(principalList);
    return subject;
  }
}
