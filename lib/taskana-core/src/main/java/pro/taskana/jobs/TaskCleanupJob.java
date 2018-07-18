package pro.taskana.jobs;

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
    private Instant completedBefore;

    // Results
    private BulkOperationResults<String, TaskanaException> results;

    public TaskCleanupJob(TaskanaEngine taskanaEngine, Instant completedBefore) {
        super(taskanaEngine);
        this.completedBefore = completedBefore;
    }

    @Override
    public void run() throws TaskanaException {
        LOGGER.info("Running job to delete all tasks completed before ({})", completedBefore.toString());
        try {
            List<TaskSummary> tasksCompletedBefore = getTasksCompletedBefore(completedBefore);
            deleteTasks(tasksCompletedBefore);
            LOGGER.info("Job ended successfully.");
        } catch (InvalidArgumentException e) {
            throw new TaskanaException("Error while processing TaskCleanupJob.", e);
        }
    }

    private List<TaskSummary> getTasksCompletedBefore(Instant untilDate) {
        return taskanaEngine.getTaskService()
            .createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .list();
    }

    private void deleteTasks(List<TaskSummary> tasksToBeDeleted) throws InvalidArgumentException {
        List<String> tasksIdsToBeDeleted = tasksToBeDeleted.stream()
            .map(task -> task.getTaskId())
            .collect(Collectors.toList());
        results = taskanaEngine.getTaskService().deleteTasks(tasksIdsToBeDeleted);
        LOGGER.info("{} tasks deleted.", tasksIdsToBeDeleted.size() - results.getFailedIds().size());
        for (String failedId : results.getFailedIds()) {
            LOGGER.warn("Task with id {} could not be deleted. Reason: {}", failedId, results.getErrorForId(failedId));
        }
    }

}
