package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if a specific {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket} does
 * have content and is requested to be deleted.
 */
public class WorkbasketInUseException extends TaskanaException {

  public WorkbasketInUseException(String msg) {
    super(msg);
  }
}
