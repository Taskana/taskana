package pro.taskana.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.impl.util.LoggerUtils;

/**
 * This class performs task updates if a classification is changed.
 *
 * @author bbr
 */
public class TaskUpdateJobExecutor implements SingleJobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskUpdateJobExecutor.class);

    private TaskanaEngineImpl taskanaEngine;
    private List<String> affectedTaskIds;

    public TaskUpdateJobExecutor() {
    }

    @Override
    public BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine) {
        this.taskanaEngine = taskanaEngine;
        Map<String, String> args = job.getArguments();
        String taskIdsString = args.get(TASKIDS);
        affectedTaskIds = Arrays.asList(taskIdsString.split(","));

        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        bulkLog.addAllErrors(handleAffectedTasks(job));

        return bulkLog;
    }

    private BulkOperationResults<String, Exception> handleAffectedTasks(Job job) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("the following tasks will be updated by the current job {}",
                LoggerUtils.listToString(affectedTaskIds));
        }
        TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngine.getTaskService();
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        for (String taskId : affectedTaskIds) {
            try {
                bulkLog.addAllErrors(taskService.refreshPriorityAndDueDate(taskId));
            } catch (Exception e) {
                bulkLog.addError(taskId, e);
            }
        }
        return bulkLog;
    }

}
