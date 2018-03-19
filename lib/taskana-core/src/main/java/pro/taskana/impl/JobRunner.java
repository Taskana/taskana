package pro.taskana.impl;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.mappings.JobMapper;

/**
 * This is the runner for all jobs scheduled in the Job table.
 *
 * @author bbr
 */
public class JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private JobMapper jobMapper;

    public JobRunner(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        jobMapper = this.taskanaEngine.getSqlSession().getMapper(JobMapper.class);
    }

    public BulkOperationResults<String, Exception> runJobs() {
        LOGGER.info("entry to runJobs()");
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        try {
            taskanaEngine.openConnection();
            List<Job> jobs = jobMapper.findJobsToRun();
            for (Job job : jobs) {
                BulkOperationResults<String, Exception> log = runSingleJob(job);
                bulkLog.addAllErrors(log);
            }
            return bulkLog;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.info("exit from runJobs(). Returning result {} ", bulkLog);
        }

    }

    private BulkOperationResults<String, Exception> runSingleJob(Job job) {
        LOGGER.debug("entry to runSingleJob(job = {})", job);
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        if (Job.State.READY.equals(job.getState())) {
            job.setStarted(Instant.now());
        }
        job.setState(Job.State.RUNNING);
        jobMapper.update(job);
        SingleJobExecutor executor;
        try {
            executor = (SingleJobExecutor) Class.forName(job.getExecutor()).newInstance();
            bulkLog = executor.runSingleJob(job, taskanaEngine);

        } catch (Exception e) {
            bulkLog.addError("JobId:" + job.getJobId(), e);
            job.setCompleted(Instant.now());
            job.setState(Job.State.FAILED);
            jobMapper.update(job);
            return bulkLog;
        }
        job.setCompleted(Instant.now());
        job.setState(Job.State.COMPLETED);
        jobMapper.update(job);

        LOGGER.debug("exit from runSingleJob");
        if (bulkLog.containsErrors()) {
            LOGGER.error("Errors occurred when running job {}.", job);
            for (String id : bulkLog.getFailedIds()) {
                LOGGER.error(id + bulkLog.getErrorForId(id));
            }
        }
        return bulkLog;
    }
}
