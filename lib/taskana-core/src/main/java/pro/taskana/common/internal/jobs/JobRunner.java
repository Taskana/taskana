package pro.taskana.common.internal.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** This is the runner for Tasks jobs. */
@Slf4j
public class JobRunner {

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
      log.error("Error running job: {} ", scheduledJob.getType(), e);
      throw new SystemException(String.format("Error running job '%s'", scheduledJob.getType()), e);
    }
  }

  private ScheduledJob lockJob(ScheduledJob job) {
    String hostAddress = getHostAddress();
    String owner = hostAddress + " - " + Thread.currentThread().getName();
    job.setLockedBy(owner);
    ScheduledJob lockedJob = jobService.lockJob(job, owner);
    if (log.isDebugEnabled()) {
      log.debug("Locked job: {}", lockedJob);
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
