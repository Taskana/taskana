package pro.taskana.common.internal.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** The JobRunner is the runner for Tasks jobs. */
public class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);
  private final TaskanaEngine taskanaEngine;
  private final JobServiceImpl jobService;
  private TaskanaTransactionProvider txProvider;

  public JobRunner(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    jobService = (JobServiceImpl) taskanaEngine.getJobService();
  }

  public void registerTransactionProvider(TaskanaTransactionProvider txProvider) {
    this.txProvider = txProvider;
  }

  public void runJobs() {
    findAndLockJobsToRun().forEach(this::runJobTransactionally);
  }

  private List<ScheduledJob> findAndLockJobsToRun() {
    return TaskanaTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> jobService.findJobsToRun().stream().map(this::lockJob).collect(Collectors.toList()));
  }

  private void runJobTransactionally(ScheduledJob scheduledJob) {
    TaskanaTransactionProvider.executeInTransactionIfPossible(
        txProvider, () -> taskanaEngine.runAsAdmin(() -> runScheduledJob(scheduledJob)));
    jobService.deleteJob(scheduledJob);
  }

  private void runScheduledJob(ScheduledJob scheduledJob) {
    try {
      AbstractTaskanaJob.createFromScheduledJob(taskanaEngine, txProvider, scheduledJob).run();
    } catch (Exception e) {
      LOGGER.error("Error running job: {} ", scheduledJob.getType(), e);
      throw new SystemException(String.format("Error running job '%s'", scheduledJob.getType()), e);
    }
  }

  private ScheduledJob lockJob(ScheduledJob job) {
    String hostAddress = getHostAddress();
    String owner = hostAddress + " - " + Thread.currentThread().getName();
    job.setLockedBy(owner);
    ScheduledJob lockedJob = jobService.lockJob(job, owner);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Locked job: {}", lockedJob);
    }
    return lockedJob;
  }

  private String getHostAddress() {
    String hostAddress;
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      hostAddress = "UNKNOWN_ADDRESS";
    }
    return hostAddress;
  }
}
