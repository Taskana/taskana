package pro.taskana.jobs;

import pro.taskana.exceptions.TaskanaException;

/**
 * Interface for all background TASKANA jobs.
 */
public interface TaskanaJob {

    /**
     * Runs the TaskanaJob.
     */
    void run() throws TaskanaException;

}
