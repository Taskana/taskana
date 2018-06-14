package pro.taskana.impl.jobs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * This is the runner for Tasks jobs.
 *
 * @author mmr
 */
public class JobTaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private TaskServiceImpl taskService;

    public JobTaskRunner(TaskanaEngine taskanaEngine, TaskService taskService) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskService = (TaskServiceImpl) taskService;
    }

    public BulkOperationResults<String, TaskanaException> runCleanCompletedTasks(Instant untilDate) {
        return cleanCompletedTasks(untilDate);
    }

    private BulkOperationResults<String, TaskanaException> cleanCompletedTasks(Instant untilDate) {
        LOGGER.info("entry to RunCompletedTasks({})", untilDate.toString());
        BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();
        try {
            List<String> tasksIds = new ArrayList<>();
            List<TaskSummary> tasksCompleted = taskService.createTaskQuery()
                .completedWithin(new TimeInterval(null, untilDate))
                .list();
            tasksCompleted.stream().forEach(task -> {
                tasksIds.add(task.getTaskId());
                LOGGER.info("task id deleted: {}", task.getTaskId());
            });
            bulkLog = taskService.deleteTasks(tasksIds);
        } catch (Exception e) {
            taskanaEngine.returnConnection();
            LOGGER.info("exit from RunCompletedTasks(). Returning result {} ", bulkLog);
        }
        LOGGER.info("exit from RunCompletedTasks({})", untilDate.toString());
        return bulkLog;

    }
}
