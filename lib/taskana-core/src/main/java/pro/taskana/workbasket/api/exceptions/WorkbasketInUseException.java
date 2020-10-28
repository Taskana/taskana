package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/** Thrown if a specific Workbasket does have content and should be deleted. */
public class WorkbasketInUseException extends TaskanaException {

  public WorkbasketInUseException(String msg) {
    super(msg);
  }
}
