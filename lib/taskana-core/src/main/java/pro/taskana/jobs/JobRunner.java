package pro.taskana.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.JobServiceImpl;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This is the runner for Tasks jobs.
 */
public class JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private JobServiceImpl jobService;
    private TaskanaTransactionProvider<Object> txProvider;
    private int maxRetryCount;
    private int attempt = 0;

    public JobRunner(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        jobService = (JobServiceImpl) taskanaEngine.getJobService();
        maxRetryCount = taskanaEngine.getConfiguration().getMaxNumberOfJobRetries();
    }

    public void registerTransactionProvider(
        TaskanaTransactionProvider<Object> txProvider) {
        this.txProvider = txProvider;
    }

    public void runJobs() {
        LOGGER.info("entry to runJobs()");
        try {
            List<ScheduledJob> jobsToRun = findAndLockJobsToRun();
            for (ScheduledJob scheduledJob : jobsToRun) {
                runJobTransactionally(scheduledJob);
            }
        } catch (Exception e) {
            LOGGER.error("Error occured whle running jobs: ", e);
        } finally {
            LOGGER.info("exit from runJobs().");
        }

    }

    private List<ScheduledJob> findAndLockJobsToRun() {
        List<ScheduledJob> availableJobs = jobService.findJobsToRun();
        List<ScheduledJob> lockedJobs = new ArrayList<ScheduledJob>();
        for (ScheduledJob job : availableJobs) {
            lockedJobs.add(lockJobTransactionally(job));
        }
        return lockedJobs;
    }

    private ScheduledJob lockJobTransactionally(ScheduledJob job) {
        ScheduledJob lockedJob = null;
        if (txProvider != null) {
            lockedJob = (ScheduledJob) txProvider.executeInTransaction(() -> {
                return lockJob(job);
            });
        } else {
            lockedJob = lockJob(job);
        }
        LOGGER.debug("Locked job: {}", lockedJob);
        return lockedJob;
    }

    private ScheduledJob lockJob(ScheduledJob job) {
        String hostAddress = "UNKNOWN_ADDRESS";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        job.setLockedBy(
            hostAddress + " - " + Thread.currentThread().getName());
        String owner = hostAddress + " - " + Thread.currentThread().getName();
        ScheduledJob lockedJob = jobService.lockJob(job, owner);
        return lockedJob;
    }

    private void runJobTransactionally(ScheduledJob scheduledJob) {
        try {
            if (txProvider != null) {
                txProvider.executeInTransaction(() -> {
                    runScheduledJob(scheduledJob);
                    return null;
                });
            } else {
                runScheduledJob(scheduledJob);
            }
            jobService.deleteJob(scheduledJob);
        } catch (Exception e) {
            e.printStackTrace();
            // transaction was rolled back -> split job into 2 half sized jobs
            LOGGER.warn(
                "Processing of job " + scheduledJob.getJobId() + " failed. Trying to split it up into two pieces...",
                e);
            // rescheduleBisectedJob(bulkLog, job);
            // List<String> objectIds;
            // if (job.getType().equals(ScheduledJob.Type.UPDATETASKSJOB)) {
            // String taskIdsAsString = job.getArguments().get(SingleJobExecutor.TASKIDS);
            // objectIds = Arrays.asList(taskIdsAsString.split(","));
            // } else if (job.getType().equals(ScheduledJob.Type.CLASSIFICATIONCHANGEDJOB)) {
            // String classificationId = job.getArguments().get(SingleJobExecutor.CLASSIFICATION_ID);
            // objectIds = Arrays.asList(classificationId);
            // } else {
            // throw new SystemException("Unknown Jobtype " + job.getType() + " encountered.");
            // }
            // for (String objectId : objectIds) {
            // bulkLog.addError(objectId, e);
            // }
            // setJobFailed(job, bulkLog);
        }
    }

    private void runScheduledJob(ScheduledJob scheduledJob) {
        LOGGER.debug("entry to runScheduledJob(job = {})", scheduledJob);
        try {
            TaskanaJob job = AbstractTaskanaJob.createFromScheduledJob(taskanaEngine, txProvider, scheduledJob);
            job.run();
        } catch (Exception e) {
            LOGGER.error("Error running job: {} ", scheduledJob.getType(), e);
            throw new SystemException(
                "When attempting to load class " + scheduledJob.getType() + " caught Exception " + e.getMessage(),
                e);
        }
        LOGGER.debug("exit from runScheduledJob");
    }

}
