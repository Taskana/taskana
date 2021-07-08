package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;

/** This exception is thrown when the current state doesn't allow the requested operation. */
public class InvalidStateException extends TaskanaException {

  protected InvalidStateException(String msg, ErrorCode errorCode) {
    super(msg, errorCode);
  }
}
