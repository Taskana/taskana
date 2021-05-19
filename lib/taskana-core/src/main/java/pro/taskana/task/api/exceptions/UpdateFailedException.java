package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if a specific {@linkplain pro.taskana.task.api.models.Task Task} is not in the database.
 */
public class UpdateFailedException extends TaskanaException {

  public UpdateFailedException(String msg) {
    super(msg);
  }
}
