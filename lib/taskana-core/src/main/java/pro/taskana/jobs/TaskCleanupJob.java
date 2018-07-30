package pro.taskana.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.TaskanaException;

/**
 * Job to cleanup completed tasks after a period of time.
 */
public class TaskCleanupJob extends AbstractTaskanaJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJob.class);

    // Parameter
    private Instant firstRun;
    private Duration runEvery;
    private Duration minimumAge;

    public TaskCleanupJob(TaskanaEngine taskanaEngine, ScheduledJob scheduledJob) {
        super(taskanaEngine, scheduledJob);
        firstRun = taskanaEngine.getConfiguration().getTaskCleanupJobFirstRun();
        runEvery = taskanaEngine.getConfiguration().getTaskCleanupJobRunEvery();
        minimumAge = taskanaEngine.getConfiguration().getTaskCleanupJobMinimumAge();
    }

    @Override
    public void run() throws TaskanaException {
        Instant completedBefore = Instant.now().minus(minimumAge);
        LOGGER.info("Running job to delete all tasks completed before ({})", completedBefore.toString());
        try {
            List<TaskSummary> tasksCompletedBefore = getTasksCompletedBefore(completedBefore);
            deleteTasks(tasksCompletedBefore);
            scheduleNextCleanupJob();
            LOGGER.info("Job ended successfully.");
        } catch (InvalidArgumentException e) {
            throw new TaskanaException("Error while processing TaskCleanupJob.", e);
        }
    }

    private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {
        return taskanaEngineImpl.getTaskService()
            .createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .list();
    }

    private void deleteTasks(List<TaskSummary> tasksToBeDeleted) throws InvalidArgumentException {
        List<String> tasksIdsToBeDeleted = tasksToBeDeleted.stream()
            .map(task -> task.getTaskId())
            .collect(Collectors.toList());
        BulkOperationResults<String, TaskanaException> results = taskanaEngineImpl.getTaskService()
            .deleteTasks(tasksIdsToBeDeleted);
        LOGGER.info("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
        for (String failedId : results.getFailedIds()) {
            LOGGER.warn("Task with id {} could not be deleted. Reason: {}", failedId, results.getErrorForId(failedId));
        }
    }

    public void scheduleNextCleanupJob() {
        LOGGER.debug("Entry to scheduleNextCleanupJob.");
        ScheduledJob job = new ScheduledJob();
        job.setType(ScheduledJob.Type.TASKCLEANUPJOB);
        job.setDue(getNextDueForTaskCleanupJob());
        taskanaEngineImpl.getJobService().createJob(job);
        LOGGER.debug("Exit from scheduleNextCleanupJob.");
    }

    private Instant getNextDueForTaskCleanupJob() {
        Instant nextRunAt = firstRun;
        while (nextRunAt.isBefore(Instant.now())) {
            nextRunAt = nextRunAt.plus(runEvery);
        }
        LOGGER.info("Scheduling next run of the TaskCleanupJob for {}", nextRunAt);
        return nextRunAt;
    }

    /**
     * Initializes the TaskCleanupJob schedule. <br>
     * All scheduled cleanup jobs are cancelled/deleted and a new one is scheduled.
     *
     * @param taskanaEngine
     */
    public static void initializeSchedule(TaskanaEngine taskanaEngine) {
        TaskCleanupJob job = new TaskCleanupJob(taskanaEngine, null);
        job.scheduleNextCleanupJob();
    }

}
