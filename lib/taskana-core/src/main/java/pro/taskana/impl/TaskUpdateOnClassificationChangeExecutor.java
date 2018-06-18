package pro.taskana.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.impl.jobs.Job;
import pro.taskana.impl.jobs.SingleJobExecutor;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.TaskMapper;

/**
 * This class performs task updates if a classification is changed.
 *
 * @author bbr
 */
public class TaskUpdateOnClassificationChangeExecutor implements SingleJobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    public static final String CLASSIFICATION_ID = "classificationId";
    public static final String PRIORITY_CHANGED = "priorityChanged";
    public static final String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";

    private TaskanaEngineImpl taskanaEngine;
    private Job job;
    private String classificationId;
    private boolean priorityChanged;
    private boolean serviceLevelChanged;
    private TaskMapper taskMapper;
    private ClassificationMapper classificationMapper;
    private AttachmentMapper attachmentMapper;

    public TaskUpdateOnClassificationChangeExecutor() {
    }

    @Override
    public BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine) {
        this.job = job;
        this.taskanaEngine = taskanaEngine;
        this.taskMapper = taskanaEngine.getSqlSession().getMapper(TaskMapper.class);
        this.classificationMapper = taskanaEngine.getSqlSession().getMapper(ClassificationMapper.class);
        this.attachmentMapper = taskanaEngine.getSqlSession().getMapper(AttachmentMapper.class);
        Map<String, String> args = job.getArguments();
        classificationId = args.get(CLASSIFICATION_ID);
        priorityChanged = Boolean.getBoolean(args.get(PRIORITY_CHANGED));
        serviceLevelChanged = Boolean.getBoolean(args.get(SERVICE_LEVEL_CHANGED));
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        bulkLog.addAllErrors(handleAffectedTasks());

        return bulkLog;
    }

    private BulkOperationResults<String, Exception> handleAffectedTasks() {
        List<TaskSummaryImpl> tasks = taskMapper.findTasksAffectedByClassificationChange(classificationId);
        List<String> taskIdsFromAttachments = attachmentMapper
            .findTaskIdsAffectedByClassificationChange(classificationId);
        List<String> filteredTaskIdsFromAttachments = taskMapper.filterTaskIdsForNotCompleted(taskIdsFromAttachments);

        Set<String> affectedTaskIds = new HashSet<>(filteredTaskIdsFromAttachments);
        for (TaskSummaryImpl task : tasks) {
            affectedTaskIds.add(task.getTaskId());
        }
        LOGGER.debug("the following tasks are affected by the update of classification {} : {}", classificationId,
            LoggerUtils.setToString(affectedTaskIds));
        TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngine.getTaskService();
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        for (String taskId : affectedTaskIds) {
            try {
                bulkLog.addAllErrors(taskService.classificationChanged(taskId, classificationId));
            } catch (Exception e) {
                bulkLog.addError(taskId, e);
            }
        }
        return bulkLog;
    }

}
