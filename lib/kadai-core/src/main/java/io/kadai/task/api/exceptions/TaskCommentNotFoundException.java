package io.kadai.task.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.models.TaskComment;
import java.util.Map;

/** This exception is thrown when a specific {@linkplain TaskComment} is not in the database. */
public class TaskCommentNotFoundException extends KadaiException {

  public static final String ERROR_KEY = "TASK_COMMENT_NOT_FOUND";
  private final String taskCommentId;

  public TaskCommentNotFoundException(String taskCommentId) {
    super(
        String.format("TaskComment with id '%s' was not found", taskCommentId),
        ErrorCode.of(ERROR_KEY, Map.of("taskCommentId", ensureNullIsHandled(taskCommentId))));
    this.taskCommentId = taskCommentId;
  }

  public String getTaskCommentId() {
    return taskCommentId;
  }
}
