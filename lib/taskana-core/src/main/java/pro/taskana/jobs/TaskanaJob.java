package pro.taskana.jobs;

import pro.taskana.exceptions.TaskanaException;

/**
 * Interface for all background TASKANA jobs.
 */
public interface TaskanaJob {

    /**
     * Runs the TaskanaJob.
     *
     * @throws TaskanaException
     *             if an exception occured during the run.
     */
    void run() throws TaskanaException;

}
