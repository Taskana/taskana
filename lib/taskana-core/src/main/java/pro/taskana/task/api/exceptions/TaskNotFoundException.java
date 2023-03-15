package pro.taskana.task.api.exceptions;

import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Task;

/** This exception is thrown when a specific {@linkplain Task} is not in the database. */
public class TaskNotFoundException extends TaskanaException {

  public static final String ERROR_KEY = "TASK_NOT_FOUND";
  private final String taskId;

  public TaskNotFoundException(String taskId) {
    super(
        String.format("Task with id '%s' was not found.", taskId),
        ErrorCode.of(ERROR_KEY, Map.of("taskId", ensureNullIsHandled(taskId))));
    this.taskId = taskId;
  }

  public String getTaskId() {
    return taskId;
  }
}
