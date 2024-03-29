package pro.taskana.task.api.exceptions;

import java.util.Arrays;
import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

/**
 * This exception is thrown when the {@linkplain Task#getState() state} of the {@linkplain Task}
 * doesn't allow the requested operation.
 */
public class InvalidTaskStateException extends TaskanaException {

  public static final String ERROR_KEY = "TASK_INVALID_STATE";
  private final String taskId;
  private final TaskState taskState;
  private final TaskState[] requiredTaskStates;

  public InvalidTaskStateException(
      String taskId, TaskState taskState, TaskState... requiredTaskStates) {
    super(
        String.format(
            "Task with id '%s' is in state: '%s', but must be in one of these states: '%s'",
            taskId, taskState, Arrays.toString(requiredTaskStates)),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("taskId", taskId),
                Map.entry("taskState", taskState),
                Map.entry("requiredTaskStates", requiredTaskStates))));

    this.taskId = taskId;
    this.taskState = taskState;
    this.requiredTaskStates = requiredTaskStates;
  }

  public String getTaskId() {
    return taskId;
  }

  public TaskState getTaskState() {
    return taskState;
  }

  public TaskState[] getRequiredTaskStates() {
    return requiredTaskStates;
  }
}
