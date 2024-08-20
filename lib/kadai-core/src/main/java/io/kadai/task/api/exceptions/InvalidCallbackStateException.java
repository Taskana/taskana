package io.kadai.task.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.CallbackState;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.MinimalTaskSummary;
import java.util.Arrays;
import java.util.Map;

/**
 * This exception is thrown when the {@linkplain MinimalTaskSummary#getCallbackState() callback
 * state} of the {@linkplain Task} doesn't allow the requested operation.
 */
public class InvalidCallbackStateException extends KadaiException {

  public static final String ERROR_KEY = "TASK_INVALID_CALLBACK_STATE";
  private final String taskId;
  private final CallbackState taskCallbackState;
  private final CallbackState[] requiredCallbackStates;

  public InvalidCallbackStateException(
      String taskId, CallbackState taskCallbackState, CallbackState... requiredCallbackStates) {
    super(
        String.format(
            "Expected callback state of Task with id '%s' to be: '%s', but found '%s'",
            taskId, Arrays.toString(requiredCallbackStates), taskCallbackState),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("taskId", ensureNullIsHandled(taskId)),
                Map.entry("taskCallbackState", ensureNullIsHandled(taskCallbackState)),
                Map.entry("requiredCallbackStates", ensureNullIsHandled(requiredCallbackStates)))));
    this.taskId = taskId;
    this.taskCallbackState = taskCallbackState;
    this.requiredCallbackStates = requiredCallbackStates;
  }

  public CallbackState getTaskCallbackState() {
    return taskCallbackState;
  }

  public CallbackState[] getRequiredCallbackStates() {
    return requiredCallbackStates;
  }

  public String getTaskId() {
    return taskId;
  }
}
