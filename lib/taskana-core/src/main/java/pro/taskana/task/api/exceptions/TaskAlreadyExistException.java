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

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
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
        ErrorCode.of(ERROR_KEY, MapCreator.of("externalTaskId", externalId)));
    this.externalId = externalId;
  }

  public String getExternalId() {
    return externalId;
  }
}
