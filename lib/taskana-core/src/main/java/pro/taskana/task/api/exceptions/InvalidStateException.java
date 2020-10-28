package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/** This exception is thrown when the task state doesn't allow the requested operation. */
public class InvalidStateException extends TaskanaException {
  private static final long serialVersionUID = 1L;

  public InvalidStateException(String msg) {
    super(msg);
  }
}
