package pro.taskana.impl;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.JobService;
import pro.taskana.TaskanaEngine;
import pro.taskana.jobs.ScheduledJob;
import pro.taskana.mappings.JobMapper;

/**
 * Controls all job activities.
 */
public class JobServiceImpl implements JobService {

    public static final Integer JOB_DEFAULT_PRIORITY = 50;
    public static final long DEFAULT_LOCK_EXPIRATION_PERIOD = 60000;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);
    private JobMapper jobMapper;
    private TaskanaEngineImpl taskanaEngineImpl;

    public JobServiceImpl(TaskanaEngine taskanaEngine, JobMapper jobMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.jobMapper = jobMapper;
    }

    @Override
    public ScheduledJob createJob(ScheduledJob job) {
        LOGGER.debug("Entry to createJob({})", job);
        try {
            taskanaEngineImpl.openConnection();
            job = initializeJobDefault(job);
            jobMapper.insertJob(job);
            LOGGER.debug("Created job {}", job);
        } finally {
            taskanaEngineImpl.returnConnection();
        }
        LOGGER.debug("Exit from createJob");
        return job;
    }

    public ScheduledJob lockJob(ScheduledJob job, String owner) {
        LOGGER.debug("entry to lockJob(jobId = {}, owner = {})", job.getJobId(), owner);
        try {
            taskanaEngineImpl.openConnection();
            job.setLockedBy(owner);
            job.setLockExpires(Instant.now().plusMillis(DEFAULT_LOCK_EXPIRATION_PERIOD));
            job.setRetryCount(job.getRetryCount() - 1);
            jobMapper.update(job);
            LOGGER.debug("Job {} locked. Remaining retries: {}", job.getJobId(), job.getRetryCount());
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from lockJob()");
        }
        return job;
    }

    public List<ScheduledJob> findJobsToRun() {
        LOGGER.debug("entry to findJobsToRun");
        List<ScheduledJob> availableJobs;
        try {
            taskanaEngineImpl.openConnection();
            availableJobs = jobMapper.findJobsToRun();
            LOGGER.debug("Found available jobs: {}", availableJobs);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from findJobsToRun()");
        }
        return availableJobs;
    }

    public void deleteJob(ScheduledJob job) {
        LOGGER.debug("entry to deleteJob(jobId = {})", job.getJobId());
        try {
            taskanaEngineImpl.openConnection();
            jobMapper.delete(job);
            LOGGER.debug("Deleted job: {}", job);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from deleteJob()");
        }
    }

    private ScheduledJob initializeJobDefault(ScheduledJob job) {
        LOGGER.debug("entry to initializeJobDefault(job = {})", job);
        job.setCreated(Instant.now());
        job.setState(ScheduledJob.State.READY);
        job.setPriority(JOB_DEFAULT_PRIORITY);
        if (job.getDue() == null) {
            job.setDue(Instant.now());
        }
        job.setRetryCount(taskanaEngineImpl.getConfiguration().getMaxNumberOfJobRetries());
        LOGGER.debug("Job after initialization: {}", job);
        return job;
    }

}
