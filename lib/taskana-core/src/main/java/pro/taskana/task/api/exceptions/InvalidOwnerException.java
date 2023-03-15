package pro.taskana.task.api.exceptions;

import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Task;

/** This exception is thrown when the current user is not the owner of the {@linkplain Task}. */
public class InvalidOwnerException extends TaskanaException {
  public static final String ERROR_KEY = "TASK_INVALID_OWNER";
  private final String taskId;
  private final String currentUserId;

  public InvalidOwnerException(String currentUserId, String taskId) {
    super(
        String.format("User '%s' is not owner of Task '%s'", currentUserId, taskId),
        ErrorCode.of(ERROR_KEY, Map.of("taskId", taskId, "currentUserId", currentUserId)));
    this.taskId = taskId;
    this.currentUserId = currentUserId;
  }

  public String getTaskId() {
    return taskId;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
