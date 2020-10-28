package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown when a Task is going to be created, but a Task with the same ID does already exist. The
 * Task ID should be unique.
 */
public class TaskAlreadyExistException extends TaskanaException {

  public TaskAlreadyExistException(String id) {
    super(id);
  }
}
