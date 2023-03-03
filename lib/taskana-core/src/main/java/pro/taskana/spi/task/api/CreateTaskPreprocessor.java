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
package pro.taskana.spi.task.api;

import pro.taskana.task.api.models.Task;

/**
 * The CreateTaskPreprocessor allows to implement customized behaviour before the given {@linkplain
 * Task} has been created.
 */
public interface CreateTaskPreprocessor {

  /**
   * Perform any action before a {@linkplain Task} has been created through {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   */
  void processTaskBeforeCreation(Task taskToProcess);
}
