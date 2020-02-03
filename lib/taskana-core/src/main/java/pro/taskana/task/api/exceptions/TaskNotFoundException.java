package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

/** This exception will be thrown if a specific task is not in the database. */
public class TaskNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public TaskNotFoundException(String id, String msg) {
    super(id, msg);
  }
}
