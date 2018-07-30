package pro.taskana.jobs;

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

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.security.UserPrincipal;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This class invokes the JobRunner periodically to schedule long running jobs.
 */
@Component
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    @Autowired
    private TaskanaEngine taskanaEngine;

    @Autowired
    TaskanaTransactionProvider<Object> springTransactionProvider;

    @PostConstruct
    public void scheduleCleanupJob() {
        LOGGER.debug("Entry to scheduleCleanupJob.");
        TaskCleanupJob.initializeSchedule(taskanaEngine);
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
        Subject.doAs(getAdminSubject(),
            new PrivilegedExceptionAction<Object>() {

                @Override
                public Object run() throws Exception {

                    try {
                        JobRunner runner = new JobRunner(taskanaEngine);
                        runner.registerTransactionProvider(springTransactionProvider);
                        LOGGER.info("Running Jobs");
                        runner.runJobs();
                        return "Successful";
                    } catch (Throwable e) {
                        throw new Exception(e);
                    }

                }

            });
    }

    private Subject getAdminSubject() {
        Subject subject = new Subject();
        List<Principal> principalList = new ArrayList<>();
        try {
            principalList
                .add(new UserPrincipal(
                    taskanaEngine.getConfiguration().getRoleMap().get(TaskanaRole.ADMIN).iterator().next()));
        } catch (Throwable t) {
            LOGGER.warn("Could not determine a configured admin user.", t);
        }
        subject.getPrincipals().addAll(principalList);
        return subject;
    }

}
