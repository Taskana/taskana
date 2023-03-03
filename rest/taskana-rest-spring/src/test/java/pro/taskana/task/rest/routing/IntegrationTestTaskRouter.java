/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.task.rest.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

public class IntegrationTestTaskRouter implements TaskRoutingProvider {

  public static final String DEFAULT_ROUTING_TARGET = "WBI:100000000000000000000000000000000002";
  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestTaskRouter.class);

  private TaskanaEngine taskanaEngine;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
  }

  @Override
  public String determineWorkbasketId(Task task) {
    return DEFAULT_ROUTING_TARGET;
  }
}
