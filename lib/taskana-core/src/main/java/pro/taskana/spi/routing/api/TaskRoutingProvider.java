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
package pro.taskana.spi.routing.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * The TaskRoutingProvider allows to determine the {@linkplain Workbasket} for a {@linkplain Task}
 * that has no {@linkplain Workbasket} on {@linkplain
 * pro.taskana.task.api.TaskService#createTask(Task) creation}.
 */
public interface TaskRoutingProvider {

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
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Determine the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket} for a given
   * {@linkplain Task}.This method will be invoked by TASKANA when it is asked to {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task) create} a {@linkplain Task} that has no
   * {@linkplain Workbasket} assigned.
   *
   * <p>If more than one TaskRoutingProvider class is registered, TASKANA calls them all and uses
   * their results only if they agree on the {@linkplain Workbasket}. This is, if more than one
   * {@linkplain Workbasket#getId() ids} are returned, TASKANA uses them only if they are identical.
   * If different ids are returned, the {@linkplain Task} will not be {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task) created}.
   *
   * <p>If the {@linkplain Workbasket} cannot be computed, the method should return NULL. If every
   * registered TaskRoutingProvider return NULL, the {@linkplain Task} will not be {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task) created}
   *
   * <p>The behaviour is undefined if this method tries to apply persistent changes to any entity.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * @param task the {@linkplain Task} for which a {@linkplain Workbasket} must be determined
   * @return the {@linkplain Workbasket#getId() id} of the {@linkplain Workbasket}
   */
  String determineWorkbasketId(Task task);
}
