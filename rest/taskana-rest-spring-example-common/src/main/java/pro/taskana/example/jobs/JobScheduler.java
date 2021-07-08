package pro.taskana.example.jobs;

import java.lang.reflect.InvocationTargetException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.jobs.JobRunner;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

/** This class invokes the JobRunner periodically to schedule long running jobs. */
@Component
public class JobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
  private final TaskanaTransactionProvider<Object> springTransactionProvider;
  private final TaskanaEngine taskanaEngine;

  @Autowired
  public JobScheduler(
      TaskanaTransactionProvider<Object> springTransactionProvider, TaskanaEngine taskanaEngine) {
    this.springTransactionProvider = springTransactionProvider;
    this.taskanaEngine = taskanaEngine;
  }

  @PostConstruct
  public void scheduleCleanupJob()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
          ClassNotFoundException {
    TaskCleanupJob.initializeSchedule(taskanaEngine);
    WorkbasketCleanupJob.initializeSchedule(taskanaEngine);

    if (taskanaEngine.isHistoryEnabled()) {
      Thread.currentThread()
          .getContextClassLoader()
          .loadClass(Type.HISTORYCLEANUPJOB.getClazz())
          .getDeclaredMethod("initializeSchedule", TaskanaEngine.class)
          .invoke(null, taskanaEngine);
    }
  }

  @Scheduled(cron = "${taskana.jobscheduler.async.cron}")
  public void triggerJobs() {
    LOGGER.info("AsyncJobs started.");
    runAsyncJobsAsAdmin();
    LOGGER.info("AsyncJobs completed.");
  }

  private void runAsyncJobsAsAdmin() {
    taskanaEngine.runAsAdmin(
        () -> {
          JobRunner runner = new JobRunner(taskanaEngine);
          runner.registerTransactionProvider(springTransactionProvider);
          LOGGER.info("Running Jobs");
          runner.runJobs();
          return "Successful";
        });
  }
}
