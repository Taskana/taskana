package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaTransactionProvider;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.InvalidArgumentException;

/**
 * This is the runner for Tasks jobs.
 *
 * @author mmr
 */
public class JobTaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private TaskServiceImpl taskanaService;
    private TaskanaTransactionProvider<BulkOperationResults<String, Exception>> txProvider;
    private int maxRetryOperations = 3;
    private int batchSize = 50;

    public JobTaskRunner(TaskanaEngine taskanaEngine) {
        this.taskanaService = (TaskServiceImpl) taskanaEngine.getTaskService();
    }

    public BulkOperationResults<String, Exception> runCleanCompletedTasks(Instant untilDate) {
        return cleanCompletedTasks(untilDate);
    }

    public void registerTransactionProvider(
        TaskanaTransactionProvider<BulkOperationResults<String, Exception>> txProvider) {
        this.txProvider = txProvider;
    }

    private BulkOperationResults<String, Exception> cleanCompletedTasks(Instant untilDate) {
        LOGGER.info("entry to RunCompletedTasks({})", untilDate.toString());
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        int attempt = 0;

        List<TaskSummary> tasksCompleted = getTasksCompleted(untilDate);
        List<String> tasksIds = new ArrayList<>();
        while (tasksCompleted.size() != 0 && attempt < maxRetryOperations) {
            tasksCompleted.stream().forEach(task -> {
                tasksIds.add(task.getTaskId());
                LOGGER.info("task id to be deleted: {}", task.getTaskId());
            });
            bulkLog = executeTransactionalDeleting(tasksIds);
            attempt = getAttempt(bulkLog, attempt);
            tasksCompleted = getTasksCompleted(untilDate);
        }

        LOGGER.info("exit from RunCompletedTasks({}). Returning result: " + bulkLog, untilDate.toString());
        return bulkLog;

    }

    private List<TaskSummary> getTasksCompleted(Instant untilDate) {
        return taskanaService.createTaskQuery()
            .completedWithin(new TimeInterval(null, untilDate))
            .list(0, batchSize);
    }

    private BulkOperationResults<String, Exception> executeTransactionalDeleting(List<String> tasksIds) {
        if (txProvider == null) {
            return doDeleteTasks(tasksIds);
        }
        return txProvider.executeInTransaction(() -> doDeleteTasks(tasksIds));
    }

    private BulkOperationResults<String, Exception> doDeleteTasks(List<String> tasksIds) {
        if (tasksIds.isEmpty()) {
            return new BulkOperationResults<>();
        }

        try {
            return taskanaService.deleteTasks(tasksIds).mapBulkOperationResults();
        } catch (InvalidArgumentException e) {
            LOGGER.error("could not delete next tasksIds batch: {}, error:" + e.getMessage(),
                String.join(",", tasksIds));
            return new BulkOperationResults<>();
        }
    }

    private int getAttempt(BulkOperationResults<String, Exception> bulkLog, int attempt) {
        if (!bulkLog.getErrorMap().isEmpty()) {
            return ++attempt;
        }
        return 0;
    }

}
