package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/** This exception will be thrown if a specific task is not in the database. */
public class UpdateFailedException extends TaskanaException {

  public UpdateFailedException(String msg) {
    super(msg);
  }
}
