package pro.taskana.impl;

import pro.taskana.BulkOperationResults;

/**
 * This interface must be implemented by classes that execut a single job.
 *
 * @author bbr
 */
public interface SingleJobExecutor {

    BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine);
}
