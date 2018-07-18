package pro.taskana.jobs;

import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.security.UserPrincipal;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This class invokes the JobRunner periodically to schedule long running jobs.
 */
@Component
public class JobScheduler {

    private final long untilDays = 14;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private static AtomicBoolean jobRunning = new AtomicBoolean(false);

    @Autowired
    private TaskanaEngine taskanaEngine;

    @Autowired
    TaskanaTransactionProvider<Object> springTransactionProvider;

    @Scheduled(cron = "${taskana.jobscheduler.async.cron}")
    public void triggerJobs() {
        LOGGER.info("AsyncJobs started.");
        boolean otherJobActive = jobRunning.getAndSet(true);
        if (!otherJobActive) {  // only one job should be active at any time
            try {
                pro.taskana.impl.JobRunner runner = new pro.taskana.impl.JobRunner(taskanaEngine);
                runner.registerTransactionProvider(springTransactionProvider);
                LOGGER.info("Running Jobs");
                BulkOperationResults<String, Exception> result = runner.runJobs();
                Map<String, Exception> errors = result.getErrorMap();
                LOGGER.info("AsyncJobs completed. Result = {} ", LoggerUtils.mapToString(errors));
            } finally {
                jobRunning.set(false);
            }
        } else {
            LOGGER.info("AsyncJobs: Don't run Jobs because already another JobRunner is running");
        }
    }

    @Scheduled(cron = "${taskana.jobscheduler.cleanup.cron}")
    public void triggerTaskCleanupJob() {
        LOGGER.info("CleanupJob started.");
        try {
            runTaskCleanupJobAsAdmin();
            LOGGER.info("CleanupJob completed.");
        } catch (PrivilegedActionException e) {
            LOGGER.error("CleanupJob failed.", e);
        }
    }

    /*
     * Creates an admin subject and runs the job using the subject.
     */
    private void runTaskCleanupJobAsAdmin() throws PrivilegedActionException {
        Subject.doAs(getAdminSubject(),
            new PrivilegedExceptionAction<Object>() {

                @Override
                public Object run() throws Exception {

                    try {
                        JobRunner runner = new JobRunner(taskanaEngine);
                        runner.registerTransactionProvider(springTransactionProvider);
                        Instant completedBefore = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                            .atZone(ZoneId.systemDefault())
                            .minusDays(untilDays)
                            .toInstant();

                        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, completedBefore);
                        runner.runJobWithRetries(job);

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
