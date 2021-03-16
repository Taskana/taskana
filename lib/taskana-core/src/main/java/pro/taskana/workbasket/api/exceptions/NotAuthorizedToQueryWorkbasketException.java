package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaRuntimeException;

/**
 * Thrown if a user is not authorized to query a {@linkplain
 * pro.taskana.workbasket.api.models.Workbasket Workbasket}.
 */
public class NotAuthorizedToQueryWorkbasketException extends TaskanaRuntimeException {

  public NotAuthorizedToQueryWorkbasketException(String msg) {
    super(msg);
  }

  public NotAuthorizedToQueryWorkbasketException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
