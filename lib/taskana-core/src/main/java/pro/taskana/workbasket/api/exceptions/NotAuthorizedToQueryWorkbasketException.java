package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.workbasket.api.models.Workbasket;

/** This exception is thrown when a user is not authorized to query a {@linkplain Workbasket}. */
public class NotAuthorizedToQueryWorkbasketException extends TaskanaRuntimeException {

  public NotAuthorizedToQueryWorkbasketException(
      String message, ErrorCode errorCode, Throwable cause) {
    super(message, errorCode, cause);
  }
}
