package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown if a request is made to insert or update a {@linkplain
 * pro.taskana.workbasket.api.models.Workbasket Workbasket} that is missing a required property.
 */
public class InvalidWorkbasketException extends TaskanaException {

  public InvalidWorkbasketException(String msg) {
    super(msg);
  }
}
