package pro.taskana.jobs;

import pro.taskana.TaskanaEngine;

/**
 * Abstract base for all background jobs of TASKANA.
 */
public abstract class AbstractTaskanaJob implements TaskanaJob {

    protected TaskanaEngine taskanaEngine;

    public AbstractTaskanaJob(TaskanaEngine taskanaEngine) {
        this.taskanaEngine = taskanaEngine;
    }

}
