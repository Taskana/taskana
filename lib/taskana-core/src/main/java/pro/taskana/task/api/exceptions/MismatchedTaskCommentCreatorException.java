package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.task.api.models.TaskComment;

/**
 * The MismatchedTaskCommentCreatorException is thrown when the current user is not the creator of
 * the {@linkplain TaskComment} it tries to modify.
 */
public class MismatchedTaskCommentCreatorException extends NotAuthorizedException {

  public static final String ERROR_KEY = "TASK_COMMENT_CREATOR_MISMATCHED";
  private final String currentUserId;
  private final String taskCommentId;

  public MismatchedTaskCommentCreatorException(String currentUserId, String taskCommentId) {
    super(
        String.format(
            "Not authorized. Current user '%s' is not the creator of TaskComment with id '%s'.",
            currentUserId, taskCommentId),
        ErrorCode.of(
            ERROR_KEY,
            MapCreator.of("currentUserId", currentUserId, "taskCommentId", taskCommentId)));

    this.currentUserId = currentUserId;
    this.taskCommentId = taskCommentId;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }

  public String getTaskCommentId() {
    return taskCommentId;
  }
}
