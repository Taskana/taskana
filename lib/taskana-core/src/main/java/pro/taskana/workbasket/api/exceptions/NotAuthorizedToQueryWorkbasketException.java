package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaRuntimeException;

/** This exception is used to communicate that a user is not authorized to query a Workbasket. */
public class NotAuthorizedToQueryWorkbasketException extends TaskanaRuntimeException {

  public NotAuthorizedToQueryWorkbasketException(String msg) {
    super(msg);
  }

  public NotAuthorizedToQueryWorkbasketException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
