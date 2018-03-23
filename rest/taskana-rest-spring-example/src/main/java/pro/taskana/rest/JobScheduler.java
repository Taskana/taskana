package pro.taskana.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.TaskanaEngine;
import pro.taskana.impl.BulkOperationResults;
import pro.taskana.impl.JobRunner;
import pro.taskana.impl.util.LoggerUtils;

/**
 * This class invokes the JobRunner periodically to schedule long running jobs.
 *
 * @author bbr
 */
@Component
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    @Autowired
    private TaskanaEngine taskanaEngine;

    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void triggerJobs() {
        JobRunner runner = new JobRunner(taskanaEngine);
        LOGGER.info("Running Jobs");
        BulkOperationResults<String, Exception> result = runner.runJobs();
        Map<String, Exception> errors = result.getErrorMap();
        LOGGER.info("Job run completed. Result = {} ", LoggerUtils.mapToString(errors));
    }

}
