package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if the {@linkplain pro.taskana.task.api.models.Task Task} state doesn't allow the
 * requested operation.
 */
public class InvalidOwnerException extends TaskanaException {

  public InvalidOwnerException(String msg) {
    super(msg);
  }
}
