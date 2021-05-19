package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

/**
 * Thrown if a specific {@linkplain pro.taskana.task.api.models.Task Task} is not in the database.
 */
public class TaskNotFoundException extends NotFoundException {

  public TaskNotFoundException(String id, String msg) {
    super(id, msg);
  }
}
