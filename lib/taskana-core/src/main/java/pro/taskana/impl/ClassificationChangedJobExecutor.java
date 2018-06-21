package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.BulkOperationResults;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.AttachmentMapper;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.JobMapper;
import pro.taskana.mappings.TaskMapper;

/**
 * This class executes a job of type CLASSIFICATIONCHANGEDJOB.
 *
 * @author bbr
 */

public class ClassificationChangedJobExecutor implements SingleJobExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationChangedJobExecutor.class);

    private TaskanaEngineImpl taskanaEngine;
    private Job job;
    private String classificationId;
    private boolean priorityChanged;
    private boolean serviceLevelChanged;
    private TaskMapper taskMapper;
    private ClassificationMapper classificationMapper;
    private AttachmentMapper attachmentMapper;

    @Override
    public BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine) {

        this.job = job;
        this.taskanaEngine = taskanaEngine;
        this.taskMapper = taskanaEngine.getSqlSession().getMapper(TaskMapper.class);
        this.classificationMapper = taskanaEngine.getSqlSession().getMapper(ClassificationMapper.class);
        this.attachmentMapper = taskanaEngine.getSqlSession().getMapper(AttachmentMapper.class);
        Map<String, String> args = job.getArguments();
        classificationId = args.get(CLASSIFICATION_ID);
        priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
        serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
        BulkOperationResults<String, Exception> bulkLog = new BulkOperationResults<>();
        bulkLog.addAllErrors(findAffectedTasksAndScheduleUpdateJobs());

        return bulkLog;

    }

    private BulkOperationResults<String, Exception> findAffectedTasksAndScheduleUpdateJobs() {
        List<TaskSummaryImpl> tasks = taskMapper.findTasksAffectedByClassificationChange(classificationId);
        List<String> taskIdsFromAttachments = attachmentMapper
            .findTaskIdsAffectedByClassificationChange(classificationId);

        List<String> filteredTaskIdsFromAttachments = taskIdsFromAttachments.isEmpty() ? new ArrayList<>()
            : taskMapper.filterTaskIdsForNotCompleted(taskIdsFromAttachments);

        Set<String> affectedTaskIds = new HashSet<>(filteredTaskIdsFromAttachments);
        for (TaskSummaryImpl task : tasks) {
            affectedTaskIds.add(task.getTaskId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("the following tasks are affected by the update of classification {} : {}", classificationId,
                LoggerUtils.setToString(affectedTaskIds));
        }
        int batchSize = taskanaEngine.getConfiguration().getMaxNumberOfTaskUpdatesPerTransaction();
        List<List<String>> affectedTaskBatches = JobRunner.partition(affectedTaskIds, batchSize);
        for (List<String> taskIdBatch : affectedTaskBatches) {
            Map<String, String> args = new HashMap<>();
            if (!taskIdBatch.isEmpty()) {
                String taskIds = String.join(",", taskIdBatch);
                args.put(ClassificationChangedJobExecutor.TASKIDS, taskIds);
                args.put(PRIORITY_CHANGED, new Boolean(priorityChanged).toString());
                args.put(SERVICE_LEVEL_CHANGED, new Boolean(serviceLevelChanged).toString());
                Job job = new Job();
                job.setCreated(Instant.now());
                job.setState(Job.State.READY);
                job.setRetryCount(0);
                job.setType(Job.Type.UPDATETASKSJOB);
                job.setExecutor(TaskUpdateJobExecutor.class.getName());
                job.setArguments(args);
                taskanaEngine.getSqlSession().getMapper(JobMapper.class).insertJob(job);
            }
        }
        return null;
    }

}
