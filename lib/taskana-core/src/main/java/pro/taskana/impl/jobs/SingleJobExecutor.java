package pro.taskana.impl.jobs;

import pro.taskana.BulkOperationResults;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * This interface must be implemented by classes that execut a single job.
 *
 * @author bbr
 */
public interface SingleJobExecutor {

    BulkOperationResults<String, Exception> runSingleJob(Job job, TaskanaEngineImpl taskanaEngine);
}
