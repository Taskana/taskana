package pro.taskana.task.api.exceptions;

import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Task;

/**
 * This exception is thrown when a {@linkplain Task} is going to be created, but a {@linkplain Task}
 * with the same {@linkplain Task#getExternalId() external id} does already exist.
 */
public class TaskAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "TASK_ALREADY_EXISTS";
  private final String externalId;

  public TaskAlreadyExistException(String externalId) {
    super(
        String.format("Task with external id '%s' already exists", externalId),
        ErrorCode.of(ERROR_KEY, Map.of("externalTaskId", ensureNullIsHandled(externalId))));
    this.externalId = externalId;
  }

  public String getExternalId() {
    return externalId;
  }
}
