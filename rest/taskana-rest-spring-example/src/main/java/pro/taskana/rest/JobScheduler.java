package pro.taskana.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaTransactionProvider;
import pro.taskana.impl.JobRunner;
import pro.taskana.impl.JobTaskRunner;
import pro.taskana.impl.util.LoggerUtils;

/**
 * This class invokes the JobRunner periodically to schedule long running jobs.
 *
 * @author bbr
 */
@Component
public class JobScheduler {

    private final long untilDays = 14;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private static AtomicBoolean jobRunning = new AtomicBoolean(false);

    @Autowired
    private TaskanaEngine taskanaEngine;

    @Autowired
    TaskanaTransactionProvider<BulkOperationResults<String, Exception>> springTransactionProvider;

    @Scheduled(fixedRate = 60000)
    public void triggerJobs() {
        boolean otherJobActive = jobRunning.getAndSet(true);
        if (!otherJobActive) {  // only one job should be active at any time
            try {
                JobRunner runner = new JobRunner(taskanaEngine);
                runner.registerTransactionProvider(springTransactionProvider);
                LOGGER.info("Running Jobs");
                BulkOperationResults<String, Exception> result = runner.runJobs();
                Map<String, Exception> errors = result.getErrorMap();
                LOGGER.info("Job run completed. Result = {} ", LoggerUtils.mapToString(errors));
            } finally {
                jobRunning.set(false);
            }
        } else {
            LOGGER.info("Don't run Jobs because already another JobRunner is running");
        }
    }

    // Run everyDay at mid night
    @Scheduled(cron = "0 0 0 * * *")
    public void triggerTaskCompletedCleanUpJob() {
        LOGGER.info("triggerTaskCompletedCleanUpJob");
        JobTaskRunner runner = new JobTaskRunner(taskanaEngine);
        runner.registerTransactionProvider(springTransactionProvider);
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(untilDays)
            .toInstant();

        BulkOperationResults<String, Exception> result = runner.runCleanCompletedTasks(completeUntilDate);
        Map<String, Exception> errors = result.getErrorMap();

        LOGGER.info("triggerTaskCompletedCleanUpJob Completed Result = {} ", LoggerUtils.mapToString(errors));
    }
}
