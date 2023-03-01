/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.task.api.exceptions;

import java.util.Arrays;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.MinimalTaskSummary;

/**
 * This exception is thrown when the {@linkplain MinimalTaskSummary#getCallbackState() callback
 * state} of the {@linkplain Task} doesn't allow the requested operation.
 */
public class InvalidCallbackStateException extends TaskanaException {

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
            MapCreator.of(
                "taskId",
                taskId,
                "taskCallbackState",
                taskCallbackState,
                "requiredCallbackStates",
                requiredCallbackStates)));
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
