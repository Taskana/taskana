package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if a {@linkplain pro.taskana.task.api.models.Task Task} is going to be created, but a
 * {@linkplain pro.taskana.task.api.models.Task Task} with the same ID does already exist. The
 * taskId should be unique.
 */
public class TaskAlreadyExistException extends TaskanaException {

  public TaskAlreadyExistException(String id) {
    super(id);
  }
}
