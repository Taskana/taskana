package io.kadai.common.internal.jobs;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.task.internal.jobs.TaskCleanupJob;
import io.kadai.task.internal.jobs.TaskUpdatePriorityJob;
import io.kadai.workbasket.internal.jobs.WorkbasketCleanupJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Schedules the {@linkplain JobRunner} based on given {@linkplain Clock} whith given {@linkplain
 * KadaiEngine}.
 *
 * <p>For running the jobs the {@linkplain PlainJavaTransactionProvider} is used.
 */
public class JobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
  private final KadaiEngine kadaiEngine;

  private final Clock clock;

  private final PlainJavaTransactionProvider plainJavaTransactionProvider;

  public JobScheduler(KadaiEngine kadaiEngine, Clock clock) {
    this.kadaiEngine = kadaiEngine;
    this.clock = clock;
    this.plainJavaTransactionProvider =
        new PlainJavaTransactionProvider(
            kadaiEngine, kadaiEngine.getConfiguration().getDataSource());
    plainJavaTransactionProvider.executeInTransaction(
        () -> {
          if (kadaiEngine.getConfiguration().isTaskCleanupJobEnabled()) {
            AbstractKadaiJob.initializeSchedule(kadaiEngine, TaskCleanupJob.class);
            LOGGER.info("Job '{}' enabled", TaskCleanupJob.class.getName());
          }
          if (kadaiEngine.getConfiguration().isTaskUpdatePriorityJobEnabled()) {
            AbstractKadaiJob.initializeSchedule(kadaiEngine, TaskUpdatePriorityJob.class);
            LOGGER.info("Job '{}' enabled", TaskUpdatePriorityJob.class.getName());
          }
          if (kadaiEngine.getConfiguration().isWorkbasketCleanupJobEnabled()) {
            AbstractKadaiJob.initializeSchedule(kadaiEngine, WorkbasketCleanupJob.class);
            LOGGER.info("Job '{}' enabled", WorkbasketCleanupJob.class.getName());
          }
          if (kadaiEngine.getConfiguration().isUserInfoRefreshJobEnabled()) {
            initJobByClassName("io.kadai.user.jobs.UserInfoRefreshJob");
          }
          if (kadaiEngine.getConfiguration().isSimpleHistoryCleanupJobEnabled()) {
            initJobByClassName("io.kadai.simplehistory.impl.jobs.HistoryCleanupJob");
          }
          kadaiEngine.getConfiguration().getCustomJobs().forEach(this::initJobByClassName);

          return "Initialized Jobs successfully";
        });
    this.clock.register(this::runAsyncJobsAsAdmin);
  }

  public void start() {
    clock.start();
  }

  public void stop() {
    clock.stop();
  }

  private void initJobByClassName(String className) throws SystemException {
    try {
      Class<?> jobClass = Thread.currentThread().getContextClassLoader().loadClass(className);
      AbstractKadaiJob.initializeSchedule(kadaiEngine, jobClass);
      LOGGER.info("Job '{}' enabled", className);
    } catch (ClassNotFoundException e) {
      throw new SystemException(String.format("Could not find class '%s'", className), e);
    }
  }

  private void runAsyncJobsAsAdmin() {
    kadaiEngine.runAsAdmin(
        () -> {
          JobRunner runner = new JobRunner(kadaiEngine);
          runner.registerTransactionProvider(plainJavaTransactionProvider);
          LOGGER.info("Running Jobs");
          runner.runJobs();
          return "Successful";
        });
  }
}
