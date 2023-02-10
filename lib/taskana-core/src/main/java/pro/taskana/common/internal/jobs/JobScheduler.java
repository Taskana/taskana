package pro.taskana.common.internal.jobs;

import java.lang.reflect.InvocationTargetException;
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
            TaskCleanupJob.initializeSchedule(taskanaEngine);
            LOGGER.info("Job '{}' enabled", TaskCleanupJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableTaskUpdatePriorityJob()) {
            TaskUpdatePriorityJob.initializeSchedule(taskanaEngine);
            LOGGER.info("Job '{}' enabled", TaskUpdatePriorityJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableWorkbasketCleanupJob()) {
            WorkbasketCleanupJob.initializeSchedule(taskanaEngine);
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
      Thread.currentThread()
          .getContextClassLoader()
          .loadClass(className)
          .getDeclaredMethod("initializeSchedule", TaskanaEngine.class)
          .invoke(null, taskanaEngine);
      LOGGER.info("Job '{}' enabled", className);
    } catch (IllegalAccessException e) {
      throw new SystemException(
          "Method initializeSchedule in class " + className + " is not public", e);
    } catch (InvocationTargetException e) {
      throw new SystemException(
          "Could not invoke Method initializeSchedule in class " + className, e);
    } catch (NoSuchMethodException e) {
      throw new SystemException(
          "Class " + className + " does not have an initializeSchedule Method", e);
    } catch (ClassNotFoundException e) {
      throw new SystemException("Could not find class " + className, e);
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
