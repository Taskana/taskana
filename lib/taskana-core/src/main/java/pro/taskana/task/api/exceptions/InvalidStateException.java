package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/** This exception is thrown when the task state doesn't allow the requested operation. */
public class InvalidStateException extends TaskanaException {

  public InvalidStateException(String msg) {
    super(msg);
  }
}
