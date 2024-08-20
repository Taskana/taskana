package io.kadai.common.internal.jobs;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.internal.JobServiceImpl;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This is the runner for Tasks jobs. */
public class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);
  private final KadaiEngine kadaiEngine;
  private final JobServiceImpl jobService;
  private KadaiTransactionProvider txProvider;

  public JobRunner(KadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
    jobService = (JobServiceImpl) kadaiEngine.getJobService();
  }

  public void registerTransactionProvider(KadaiTransactionProvider txProvider) {
    this.txProvider = txProvider;
  }

  public void runJobs() {
    findAndLockJobsToRun().forEach(this::runJobTransactionally);
  }

  private List<ScheduledJob> findAndLockJobsToRun() {
    return KadaiTransactionProvider.executeInTransactionIfPossible(
        txProvider, () -> jobService.findJobsToRun().stream().map(this::lockJob).toList());
  }

  private void runJobTransactionally(ScheduledJob scheduledJob) {
    KadaiTransactionProvider.executeInTransactionIfPossible(
        txProvider,
        () -> {
          boolean successful = kadaiEngine.runAsAdmin(() -> runScheduledJob(scheduledJob));
          if (successful) {
            jobService.deleteJob(scheduledJob);
          }
        });
  }

  private boolean runScheduledJob(ScheduledJob scheduledJob) {
    try {
      AbstractKadaiJob.createFromScheduledJob(kadaiEngine, txProvider, scheduledJob).run();
      return true;
    } catch (Exception e) {
      LOGGER.error("Error running job: {} ", scheduledJob.getType(), e);
      return false;
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
