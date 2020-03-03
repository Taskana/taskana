package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.TaskanaException;

/**
 * Thrown when a TaskComment is going to be created, but a TaskComment with
 * the same ID does already exist. The TaskComment ID should be unique.
 */
public class TaskCommentAlreadyExistException extends TaskanaException {

  private static final long serialVersionUID = 1L;

  public TaskCommentAlreadyExistException(String id) {
    super(id);
  }

}
