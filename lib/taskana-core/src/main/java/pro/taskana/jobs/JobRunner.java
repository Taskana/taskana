package pro.taskana.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.impl.TaskServiceImpl;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * This is the runner for Tasks jobs.
 */
public class JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private TaskanaEngine taskanaEngine;
    private TaskanaTransactionProvider<Object> txProvider;
    private int batchSize = 50;
    private int maxRetryCount;
    private int attempt = 0;

    public JobRunner(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = taskanaEngine;
        maxRetryCount = taskanaEngine.getConfiguration().getMaxNumberOfJobRetries();
        batchSize = taskanaEngine.getConfiguration().getMaxNumberOfTaskUpdatesPerTransaction();
    }

    public void registerTransactionProvider(
        TaskanaTransactionProvider<Object> txProvider) {
        this.txProvider = txProvider;
    }

    public void runJob(TaskanaJob job) throws Exception {
        if (txProvider != null) {
            txProvider.executeInTransaction(() -> {
                try {
                    job.run();
                    return null;
                } catch (Exception e) {
                    LOGGER.warn("Exception caught while processing job transactionally. ", e);
                    throw new RuntimeException(e);
                }
            });
        } else {
            job.run();
        }
    }

    public void runJobWithRetries(TaskanaJob job) throws Exception {
        try {
            runJob(job);
        } catch (Exception e) {
            LOGGER.warn("Job failed due to an Exception.", e);
            if (attempt < maxRetryCount) {
                attempt++;
                LOGGER.info("Retrying for the {} time.", attempt);
                runJobWithRetries(job);
            } else {
                throw e;
            }
        }
    }

}
