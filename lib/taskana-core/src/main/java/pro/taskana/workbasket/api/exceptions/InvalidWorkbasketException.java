package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * This exception is thrown when a request is made to insert or update a workbasket that is missing
 * a required property.
 */
public class InvalidWorkbasketException extends TaskanaException {

  public InvalidWorkbasketException(String msg) {
    super(msg);
  }
}
