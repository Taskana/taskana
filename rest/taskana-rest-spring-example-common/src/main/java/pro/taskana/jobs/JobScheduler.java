package pro.taskana.jobs;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.security.UserPrincipal;
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
    LOGGER.debug("Entry to scheduleCleanupJob.");
    TaskCleanupJob.initializeSchedule(taskanaEngine);
    WorkbasketCleanupJob.initializeSchedule(taskanaEngine);

    if (taskanaEngine.isHistoryEnabled()) {
      Thread.currentThread()
          .getContextClassLoader()
          .loadClass(Type.HISTORYCLEANUPJOB.getClazz())
          .getDeclaredMethod("initializeSchedule", TaskanaEngine.class)
          .invoke(null, taskanaEngine);
    }
    LOGGER.debug("Exit from scheduleCleanupJob.");
  }

  @Scheduled(cron = "${taskana.jobscheduler.async.cron}")
  public void triggerJobs() {
    LOGGER.info("AsyncJobs started.");
    try {
      runAsyncJobsAsAdmin();
      LOGGER.info("AsyncJobs completed.");
    } catch (PrivilegedActionException e) {
      LOGGER.info("AsyncJobs failed.", e);
    }
  }

  /*
   * Creates an admin subject and runs the job using the subject.
   */
  private void runAsyncJobsAsAdmin() throws PrivilegedActionException {
    PrivilegedExceptionAction<Object> jobs =
        () -> {
          try {
            JobRunner runner = new JobRunner(taskanaEngine);
            runner.registerTransactionProvider(springTransactionProvider);
            LOGGER.info("Running Jobs");
            runner.runJobs();
            return "Successful";
          } catch (Throwable e) {
            throw new Exception(e);
          }
        };
    Subject.doAs(getAdminSubject(), jobs);
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
    } catch (Throwable t) {
      LOGGER.warn("Could not determine a configured admin user.", t);
    }
    subject.getPrincipals().addAll(principalList);
    return subject;
  }
}
