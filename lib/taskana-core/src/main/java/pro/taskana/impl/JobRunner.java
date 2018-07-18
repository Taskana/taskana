package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.JobMapper;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This is the runner for all jobs scheduled in the Job table.
 *
 * @author bbr
 */
public class JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);
    private TaskanaEngineImpl taskanaEngine;
    private JobMapper jobMapper;
    private int maxRetryCount;
    private TaskanaTransactionProvider<Object> txProvider;

    public JobRunner(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        jobMapper = this.taskanaEngine.getSqlSession().getMapper(JobMapper.class);
        maxRetryCount = taskanaEngine.getConfiguration().getMaxNumberOfJobRetries();
        txProvider = null;
    }

    public void registerTransactionProvider(
        TaskanaTransactionProvider<Object> txProvider) {
        this.txProvider = txProvider;
    }

    public BulkOperationResults<String, Exception> runJobs() {
        LOGGER.info("entry to runJobs()");
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        Job currentlyProcessedJob = null;
        try {
            List<Job> jobs = findJobsToRun();
            while (!jobs.isEmpty()) {  // run as long as Jobs are available for processing
                for (Job job : jobs) {
                    currentlyProcessedJob = job;
                    processAJob(bulkLog, job);
                }
                jobs = findJobsToRun();
            }
            return bulkLog;
        } catch (Exception e) {
            if (currentlyProcessedJob != null) {
                bulkLog.addError("JobId:" + currentlyProcessedJob.getJobId(), e);
                setJobFailed(currentlyProcessedJob, bulkLog);
                return bulkLog;
            } else {
                LOGGER.error("tried to run jobs and caught exception {} ", e);
                bulkLog.addError("unknown", e);
                return bulkLog;
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.info("exit from runJobs(). Returning result {} ", bulkLog);
        }

    }

    private List<Job> findJobsToRun() {
        final List<Job> result = new ArrayList<>();
        if (txProvider != null) {
            txProvider.executeInTransaction(() -> {   // each job in its own transaction
                try {
                    taskanaEngine.openConnection();
                    doFindJobsToRun(result);
                    return null;
                } finally {
                    taskanaEngine.returnConnection();
                }
            });
        } else {
            doFindJobsToRun(result);
        }
        return result;
    }

    private BulkOperationResults<String, Exception> doFindJobsToRun(List<Job> jobs) {
        List<Job> found = taskanaEngine.getSqlSession().getMapper(JobMapper.class).findJobsToRun();
        jobs.addAll(found);
        return null;
    }

    private void processAJob(BulkOperationResults<String, Exception> bulkLog, Job job) {
        BulkOperationResults<String, Exception> log;
        try {
            if (txProvider != null) {
                log = (BulkOperationResults<String, Exception>) txProvider.executeInTransaction(() -> {   // each job in
                                                                                                          // its own
                                                                                                          // transaction
                    try {
                        taskanaEngine.openConnection();
                        return runSingleJob(job);
                    } finally {
                        taskanaEngine.returnConnection();
                    }
                });

            } else {
                log = runSingleJob(job);
            }
            if (log != null && log.containsErrors()
                && Job.Type.UPDATETASKSJOB.equals(job.getType())) {
                handleRetryForFailuresFromBulkOperationResult(bulkLog, job, log);
            }
        } catch (Exception e) {
            // transaction was rolled back -> split job into 2 half sized jobs
            LOGGER.warn("Processing of job " + job.getJobId() + " failed. Trying to split it up into two pieces...", e);
            if (job.getRetryCount() < maxRetryCount) {
                rescheduleBisectedJob(bulkLog, job);
            } else {
                List<String> objectIds;
                if (job.getType().equals(Job.Type.UPDATETASKSJOB)) {
                    String taskIdsAsString = job.getArguments().get(SingleJobExecutor.TASKIDS);
                    objectIds = Arrays.asList(taskIdsAsString.split(","));
                } else if (job.getType().equals(Job.Type.CLASSIFICATIONCHANGEDJOB)) {
                    String classificationId = job.getArguments().get(SingleJobExecutor.CLASSIFICATION_ID);
                    objectIds = Arrays.asList(classificationId);
                } else {
                    throw new SystemException("Unknown Jobtype " + job.getType() + " encountered.");
                }
                for (String objectId : objectIds) {
                    bulkLog.addError(objectId, e);
                }
                setJobFailed(job, bulkLog);
            }
        }
    }

    private void setJobFailed(Job job, BulkOperationResults<String, Exception> bulkLog) {
        try {
            if (txProvider != null) {
                txProvider.executeInTransaction(() -> {  // each job in its own transaction
                    try {
                        taskanaEngine.openConnection();
                        return doSetJobFailed(job, bulkLog);
                    } finally {
                        taskanaEngine.returnConnection();
                    }
                });
            } else {
                doSetJobFailed(job, bulkLog);
            }
        } catch (Exception e) {
            // transaction was rolled back -> log an Error
            LOGGER.error("attempted to set job {} to failed, but caught Exception {}", job, e);
        }

    }

    private BulkOperationResults<String, Exception> doSetJobFailed(Job job,
        BulkOperationResults<String, Exception> bulkLog) {
        job.setState(Job.State.FAILED);
        if (job.getStarted() == null) {
            job.setStarted(Instant.now());
        }
        if (bulkLog.containsErrors()) {
            Map<String, Exception> errors = bulkLog.getErrorMap();
            job.setErrors(LoggerUtils.mapToString(errors));
        }
        taskanaEngine.getSqlSession().getMapper(JobMapper.class).update(job);
        return null;
    }

    private void handleRetryForFailuresFromBulkOperationResult(BulkOperationResults<String, Exception> bulkLog, Job job,
        BulkOperationResults<String, Exception> errorLogForThisJob) {
        if (job.getRetryCount() < maxRetryCount) {
            if (errorLogForThisJob.containsErrors()) {
                List<String> failedTasks = errorLogForThisJob.getFailedIds();
                if (!failedTasks.isEmpty()) {  // some tasks failed to be processed
                    LOGGER.error("Errors occurred when running job {}. Processing will be retried", job);
                    scheduleRetryJob(job, failedTasks);
                }
            }
        } else {
            bulkLog.addAllErrors(errorLogForThisJob);
            setJobFailed(job, errorLogForThisJob);
        }
    }

    private void rescheduleBisectedJob(BulkOperationResults<String, Exception> bulkLog, Job job) {
        // the transaction that processed the job was rolled back.
        try {
            if (txProvider != null) {
                txProvider.executeInTransaction(() -> {  // each job in its own transaction
                    try {
                        taskanaEngine.openConnection();
                        return doRescheduleBisectedJob(job);
                    } finally {
                        taskanaEngine.returnConnection();
                    }
                });
            } else {
                doRescheduleBisectedJob(job);
            }
        } catch (Exception e) {
            // transaction was rolled back -> log an Error
            LOGGER.error("attempted to reschedule bisected jobs for {}, but caught Exception {}", job, e);
        }

    }

    private BulkOperationResults<String, Exception> doRescheduleBisectedJob(Job job) {
        if (job.getType().equals(Job.Type.UPDATETASKSJOB)) { // split the job in halves
            Map<String, String> args = job.getArguments();
            String taskIdsString = args.get(SingleJobExecutor.TASKIDS);
            List<String> taskIds = Arrays.asList(taskIdsString.split(","));
            int size = taskIds.size();
            if (size >= 2) {
                int halfSize = size % 2 == 0 ? size / 2 : (size / 2 + 1);
                List<List<String>> taskIdListsForNewJobs = partition(taskIds, halfSize);
                // now schedule new tasks

                for (List<String> halfSizedTaskIds : taskIdListsForNewJobs) {
                    Job newJob = new Job();
                    newJob.setCreated(Instant.now());
                    if (halfSize > 1) {
                        newJob.setRetryCount(0);
                    } else {
                        newJob.setRetryCount(job.getRetryCount() + 1);
                    }
                    newJob.setState(Job.State.READY);
                    newJob.setType(job.getType());
                    args.put(SingleJobExecutor.TASKIDS, String.join(",", halfSizedTaskIds));
                    newJob.setArguments(args);
                    newJob.setCreated(Instant.now());
                    newJob.setExecutor(job.getExecutor());
                    taskanaEngine.getSqlSession().getMapper(JobMapper.class).insertJob(newJob);
                }
                LOGGER.debug("doRescheduleBisectedJob deleting job {} ", job);
                taskanaEngine.getSqlSession().getMapper(JobMapper.class).delete(job);
            }
        } else { // take care that the job is re-executed
            job.setState(Job.State.READY);
            job.setRetryCount(job.getRetryCount() + 1);
            taskanaEngine.getSqlSession().getMapper(JobMapper.class).update(job);
        }
        return null;
    }

    private void scheduleRetryJob(Job job, List<String> failedTasks) {
        if (job.getType().equals(Job.Type.UPDATETASKSJOB)) {
            try {
                if (txProvider != null) {
                    txProvider.executeInTransaction(() -> {  // each job in its own transaction
                        try {
                            taskanaEngine.openConnection();
                            return doScheduleRetryJob(job, failedTasks);
                        } finally {
                            taskanaEngine.returnConnection();
                        }
                    });
                } else {
                    doScheduleRetryJob(job, failedTasks);
                }
            } catch (Exception e) {
                // transaction was rolled back -> log an Error
                LOGGER.error("attempted to reschedule bisected jobs for {}, but caught Exception {}", job, e);
            }
        }
    }

    private BulkOperationResults<String, Exception> doScheduleRetryJob(Job job, List<String> failedTasks) {
        LOGGER.debug("entry to doScheduleRetryJob for job {} and failedTasks {}", job,
            LoggerUtils.listToString(failedTasks));
        Map<String, String> args = job.getArguments();
        Job newJob = new Job();
        newJob.setCreated(Instant.now());
        newJob.setRetryCount(job.getRetryCount() + 1);
        newJob.setState(Job.State.READY);
        newJob.setType(job.getType());
        args.put(SingleJobExecutor.TASKIDS, String.join(",", failedTasks));
        newJob.setArguments(args);
        newJob.setExecutor(job.getExecutor());
        taskanaEngine.getSqlSession().getMapper(JobMapper.class).insertJob(newJob);
        LOGGER.debug("doScheduleRetryJob deleting job {} and scheduling {} ", job, newJob);
        taskanaEngine.getSqlSession().getMapper(JobMapper.class).delete(job);
        return null;
    }

    private BulkOperationResults<String, Exception> runSingleJob(Job job) {
        LOGGER.debug("entry to runSingleJob(job = {})", job);
        BulkOperationResults<String, Exception> bulkLog;
        if (job.getStarted() == null) {
            job.setStarted(Instant.now());
        }
        job.setState(Job.State.RUNNING);
        jobMapper.update(job);
        SingleJobExecutor executor;
        try {
            executor = (SingleJobExecutor) Class.forName(job.getExecutor()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error("When attempting to load class {} caught Exception {} ", job.getExecutor(), e);
            throw new SystemException(
                "When attempting to load class " + job.getExecutor() + " caught Exception " + e.getMessage(),
                e);
        }
        bulkLog = executor.runSingleJob(job, taskanaEngine);

        if (!bulkLog.containsErrors()) {
            LOGGER.debug("runSingleJob deletin job {} ", job);
            jobMapper.delete(job);
        }

        LOGGER.debug("exit from runSingleJob");
        return bulkLog;
    }

    static <T> List<List<T>> partition(Collection<T> members, int maxSize) {
        List<List<T>> result = new ArrayList<>();
        List<T> internal = new ArrayList<>();
        for (T member : members) {
            internal.add(member);
            if (internal.size() == maxSize) {
                result.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty()) {
            result.add(internal);
        }
        return result;
    }

}
