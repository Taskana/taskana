package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if the {@linkplain pro.taskana.task.api.models.Task Task} state doesn't allow the
 * requested operation.
 */
public class InvalidStateException extends TaskanaException {

  public InvalidStateException(String msg) {
    super(msg);
  }
}
