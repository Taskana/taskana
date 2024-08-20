package io.kadai.task.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.models.TaskComment;
import java.util.Map;

/**
 * This exception is thrown when the current user is not the creator of the {@linkplain TaskComment}
 * it tries to modify.
 */
public class NotAuthorizedOnTaskCommentException extends KadaiException {

  public static final String ERROR_KEY = "NOT_AUTHORIZED_ON_TASK_COMMENT";
  private final String currentUserId;
  private final String taskCommentId;

  public NotAuthorizedOnTaskCommentException(String currentUserId, String taskCommentId) {
    super(
        String.format(
            "Not authorized. Current user '%s' is not the creator of TaskComment with id '%s'.",
            currentUserId, taskCommentId),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("currentUserId", ensureNullIsHandled(currentUserId)),
                Map.entry("taskCommentId", ensureNullIsHandled(taskCommentId)))));

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
