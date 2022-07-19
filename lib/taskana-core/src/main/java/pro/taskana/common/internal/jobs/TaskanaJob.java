package pro.taskana.common.internal.jobs;

import pro.taskana.common.api.exceptions.TaskanaException;

/** Interface for all background TASKANA jobs. */
public interface TaskanaJob {

  /**
   * Execute the TaskanaJob.
   *
   * @throws TaskanaException if any exception occurs during the execution
   */
  void run() throws TaskanaException;
}
