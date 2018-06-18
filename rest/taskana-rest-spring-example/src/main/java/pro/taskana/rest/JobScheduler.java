package pro.taskana.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.jobs.JobRunner;
import pro.taskana.impl.jobs.JobTaskRunner;
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
    @Autowired
    private TaskanaEngine taskanaEngine;
    @Autowired
    private TaskService taskService;

    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void triggerJobs() {
        JobRunner runner = new JobRunner(taskanaEngine);
        LOGGER.info("Running Jobs");
        BulkOperationResults<String, Exception> result = runner.runJobs();
        Map<String, Exception> errors = result.getErrorMap();
        LOGGER.info("Job run completed. Result = {} ", LoggerUtils.mapToString(errors));
    }

    // Run everyDay at mid night
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void triggerTaskCompletedCleanUpJob() {
        LOGGER.info("triggerTaskCompletedCleanUpJob");
        JobTaskRunner runner = new JobTaskRunner(taskanaEngine, taskanaEngine.getTaskService());
        Instant completeUntilDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .minusDays(untilDays)
            .toInstant();

        BulkOperationResults<String, TaskanaException> result = runner.runCleanCompletedTasks(completeUntilDate);
        Map<String, TaskanaException> errors = result.getErrorMap();

        LOGGER.info("triggerTaskCompletedCleanUpJob Completed Result = {} ", LoggerUtils.mapToString(errors));
    }

}
