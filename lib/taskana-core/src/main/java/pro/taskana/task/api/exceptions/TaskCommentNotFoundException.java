package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.task.api.models.TaskComment;

/** This exception is thrown when a specific {@linkplain TaskComment} is not in the database. */
public class TaskCommentNotFoundException extends TaskanaException {

  public static final String ERROR_KEY = "TASK_COMMENT_NOT_FOUND";
  private final String taskCommentId;

  public TaskCommentNotFoundException(String taskCommentId) {
    super(
        String.format("TaskComment with id '%s' was not found", taskCommentId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("taskCommentId", taskCommentId)));
    this.taskCommentId = taskCommentId;
  }

  public String getTaskCommentId() {
    return taskCommentId;
  }
}
