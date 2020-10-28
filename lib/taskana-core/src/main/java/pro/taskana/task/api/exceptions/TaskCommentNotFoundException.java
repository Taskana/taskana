package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.NotFoundException;

/** This exception will be thrown if a specific task comment is not in the database. */
public class TaskCommentNotFoundException extends NotFoundException {

  public TaskCommentNotFoundException(String id, String msg) {
    super(id, msg);
  }
}
