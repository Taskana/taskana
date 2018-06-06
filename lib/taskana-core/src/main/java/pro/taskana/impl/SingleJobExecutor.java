package pro.taskana.impl;

import pro.taskana.BulkOperationResults;

/**
 * This interface must be implemented by classes that execut a single job.
 *
 * @author bbr
 */
public interface SingleJobExecutor {

    String TASKIDS = "taskIds";
    String CLASSIFICATION_ID = "classificationId";
    String PRIORITY_CHANGED = "priorityChanged";
    String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";

    BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine);
}
