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

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/**
 * The BeforeRequestReviewProvider allows to implement customized behaviour before a review has been
 * requested on a given {@linkplain Task}.
 */
public interface BeforeRequestReviewProvider {

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
   * Perform any action before a review has been requested on a {@linkplain Task} through
   * {@linkplain pro.taskana.task.api.TaskService#requestReview(String)} or {@linkplain
   * pro.taskana.task.api.TaskService#forceRequestReview(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#requestReview(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#requestReview(String)}.
   *
   * @param task the {@linkplain Task} before {@linkplain
   *     pro.taskana.task.api.TaskService#requestReview(String)} or {@linkplain
   *     pro.taskana.task.api.TaskService#forceRequestReview(String)} has started
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   * @throws Exception if the service provider throws any exception
   */
  Task beforeRequestReview(Task task) throws Exception;
}
