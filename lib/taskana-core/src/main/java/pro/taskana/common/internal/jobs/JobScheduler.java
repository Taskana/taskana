package pro.taskana.common.internal.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskUpdatePriorityJob;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

/**
 * Schedules the {@linkplain JobRunner} based on given {@linkplain Clock} whith given {@linkplain
 * TaskanaEngine}.
 *
 * <p>For running the jobs the {@linkplain PlainJavaTransactionProvider} is used.
 */
public class JobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
  private final TaskanaEngine taskanaEngine;

  private final Clock clock;

  private final PlainJavaTransactionProvider plainJavaTransactionProvider;

  public JobScheduler(TaskanaEngine taskanaEngine, Clock clock) {
    this.taskanaEngine = taskanaEngine;
    this.clock = clock;
    this.plainJavaTransactionProvider =
        new PlainJavaTransactionProvider(
            taskanaEngine, taskanaEngine.getConfiguration().getDatasource());
    plainJavaTransactionProvider.executeInTransaction(
        () -> {
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableTaskCleanupJob()) {
            AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskCleanupJob.class);
            LOGGER.info("Job '{}' enabled", TaskCleanupJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableTaskUpdatePriorityJob()) {
            AbstractTaskanaJob.initializeSchedule(taskanaEngine, TaskUpdatePriorityJob.class);
            LOGGER.info("Job '{}' enabled", TaskUpdatePriorityJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableWorkbasketCleanupJob()) {
            AbstractTaskanaJob.initializeSchedule(taskanaEngine, WorkbasketCleanupJob.class);
            LOGGER.info("Job '{}' enabled", WorkbasketCleanupJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableUserInfoRefreshJob()) {
            initJobByClassName("pro.taskana.user.jobs.UserInfoRefreshJob");
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableHistorieCleanupJob()) {
            initJobByClassName("pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob");
          }
          taskanaEngine
              .getConfiguration()
              .getJobSchedulerCustomJobs()
              .forEach(this::initJobByClassName);

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
      AbstractTaskanaJob.initializeSchedule(taskanaEngine, jobClass);
      LOGGER.info("Job '{}' enabled", className);
    } catch (ClassNotFoundException e) {
      throw new SystemException(String.format("Could not find class '%s'", className), e);
    }
  }

  private void runAsyncJobsAsAdmin() {
    taskanaEngine.runAsAdmin(
        () -> {
          JobRunner runner = new JobRunner(taskanaEngine);
          runner.registerTransactionProvider(plainJavaTransactionProvider);
          LOGGER.info("Running Jobs");
          runner.runJobs();
          return "Successful";
        });
  }
}
