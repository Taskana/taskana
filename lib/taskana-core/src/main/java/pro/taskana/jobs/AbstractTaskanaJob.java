package pro.taskana.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.TaskanaException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.transaction.TaskanaTransactionProvider;

/**
 * Abstract base for all background jobs of TASKANA.
 */
public abstract class AbstractTaskanaJob implements TaskanaJob {

    protected TaskanaEngineImpl taskanaEngineImpl;
    protected TaskanaTransactionProvider<Object> txProvider;
    protected ScheduledJob scheduledJob;

    public AbstractTaskanaJob(TaskanaEngine taskanaEngine, TaskanaTransactionProvider<Object> txProvider,
        ScheduledJob job) {
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.txProvider = txProvider;
        this.scheduledJob = job;
    }

    public static TaskanaJob createFromScheduledJob(TaskanaEngine engine, TaskanaTransactionProvider<Object> txProvider,
        ScheduledJob job) throws TaskanaException {
        switch (job.getType()) {
            case CLASSIFICATIONCHANGEDJOB:
                return new ClassificationChangedJob(engine, txProvider, job);
            case UPDATETASKSJOB:
                return new TaskRefreshJob(engine, txProvider, job);
            case TASKCLEANUPJOB:
                return new TaskCleanupJob(engine, txProvider, job);
            default:
                throw new TaskanaException(
                    "No matching job found for " + job.getType() + " of ScheduledJob " + job.getJobId() + ".");
        }
    }

    <T> List<List<T>> partition(Collection<T> members, int maxSize) {
        List<List<T>> result = new ArrayList<>();
        List<T> internal = new ArrayList<>();
        for (T member : members) {
            internal.add(member);
            if (internal.size() == maxSize) {
                result.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty()) {
            result.add(internal);
        }
        return result;
    }

}
