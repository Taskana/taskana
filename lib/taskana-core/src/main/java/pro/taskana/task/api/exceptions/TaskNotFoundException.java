package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.task.api.models.Task;

/** The TaskNotFoundException is thrown when a specific {@linkplain Task} is not in the database. */
public class TaskNotFoundException extends NotFoundException {

  public static final String ERROR_KEY = "TASK_NOT_FOUND";
  private final String taskId;

  public TaskNotFoundException(String taskId) {
    super(
        String.format("Task with id '%s' was not found.", taskId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("taskId", taskId)));
    this.taskId = taskId;
  }

  public String getTaskId() {
    return taskId;
  }
}
