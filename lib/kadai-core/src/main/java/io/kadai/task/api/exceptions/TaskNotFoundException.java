package io.kadai.task.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.models.Task;
import java.util.Map;

/** This exception is thrown when a specific {@linkplain Task} is not in the database. */
public class TaskNotFoundException extends KadaiException {

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
