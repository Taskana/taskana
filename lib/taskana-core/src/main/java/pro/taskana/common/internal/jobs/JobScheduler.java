package pro.taskana.common.internal.jobs;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class JobScheduler {

  public static final String LOG_MSG_JOB_ENABLED = "Job '{}' enabled";
  private final TaskanaEngine taskanaEngine;

  private final Clock clock;

  private final PlainJavaTransactionProvider plainJavaTransactionProvider;

  public JobScheduler(TaskanaEngine taskanaEngine, Clock clock) {
    this.taskanaEngine = taskanaEngine;
    this.clock = clock;
    this.plainJavaTransactionProvider =
        new PlainJavaTransactionProvider(
            taskanaEngine, taskanaEngine.getConfiguration().getDataSource());
    plainJavaTransactionProvider.executeInTransaction(
        () -> {
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableTaskCleanupJob()) {
            TaskCleanupJob.initializeSchedule(taskanaEngine);
            log.info(LOG_MSG_JOB_ENABLED, TaskCleanupJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableTaskUpdatePriorityJob()) {
            TaskUpdatePriorityJob.initializeSchedule(taskanaEngine);
            log.info(LOG_MSG_JOB_ENABLED, TaskUpdatePriorityJob.class.getName());
          }
          if (taskanaEngine.getConfiguration().isJobSchedulerEnableWorkbasketCleanupJob()) {
            WorkbasketCleanupJob.initializeSchedule(taskanaEngine);
            log.info(LOG_MSG_JOB_ENABLED, WorkbasketCleanupJob.class.getName());
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
      log.info(LOG_MSG_JOB_ENABLED, className);
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
          log.info("Running Jobs");
          runner.runJobs();
          return "Successful";
        });
  }
}
