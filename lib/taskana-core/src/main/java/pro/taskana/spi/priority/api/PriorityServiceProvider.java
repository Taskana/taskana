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
package pro.taskana.spi.priority.api;

import java.util.OptionalInt;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/**
 * The PriorityServiceProvider allows to determine the priority of a {@linkplain Task} according to
 * custom logic.
 */
public interface PriorityServiceProvider {

  /**
   * Provide the active {@linkplain TaskanaEngine} which is initialized for this TASKANA
   * installation.
   *
   * <p>This method is called during TASKANA startup and allows the service provider to store the
   * active {@linkplain TaskanaEngine} for later usage.
   *
   * @param taskanaEngine the active {@linkplain TaskanaEngine} which is initialized for this
   *     installation
   */
  default void initialize(TaskanaEngine taskanaEngine) {}

  /**
   * Determine the {@linkplain Task#getPriority() priority} of a certain {@linkplain Task} during
   * execution of {@linkplain pro.taskana.task.api.TaskService#createTask(Task)} and {@linkplain
   * pro.taskana.task.api.TaskService#updateTask(Task)}. This priority overwrites the priority from
   * Classification-driven logic.
   *
   * <p>The implemented method must calculate the {@linkplain Task#getPriority() priority}
   * efficiently. There can be a huge amount of {@linkplain Task Tasks} the SPI has to handle.
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)} or {@linkplain
   * pro.taskana.task.api.TaskService#updateTask(Task)}.
   *
   * @param taskSummary the {@linkplain TaskSummary} to compute the {@linkplain Task#getPriority()
   *     priority} for
   * @return the computed {@linkplain Task#getPriority() priority}
   */
  OptionalInt calculatePriority(TaskSummary taskSummary);
}
