package pro.taskana.jobs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This class executes a job of type CLASSIFICATIONCHANGEDJOB.
 *
 * @author bbr
 */

public class TaskRefreshJob extends AbstractTaskanaJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRefreshJob.class);

    public static final String ARG_TASK_IDS = "taskIds";

    private List<String> affectedTaskIds;

    public TaskRefreshJob(TaskanaEngine engine, TaskanaTransactionProvider<Object> txProvider, ScheduledJob job) {
        super(engine, txProvider, job);
        Map<String, String> args = job.getArguments();
        String taskIdsString = args.get(ARG_TASK_IDS);
        affectedTaskIds = Arrays.asList(taskIdsString.split(","));
    }

    @Override
    public void run() throws TaskanaException {
        LOGGER.info("Running TaskRefreshJob for {} tasks", affectedTaskIds.size());
        try {
            TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
            for (String taskId : affectedTaskIds) {
                try {
                    taskService.refreshPriorityAndDueDate(taskId);
                } catch (Exception e) {
                    LOGGER.warn("Task {} could not be refreshed because of exception: {}", taskId, e.getMessage());
                }
            }
            LOGGER.info("TaskRefreshJob ended successfully.");
        } catch (Exception e) {
            throw new TaskanaException("Error while processing TaskRefreshJob.", e);
        }
    }

}
