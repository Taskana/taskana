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
package pro.taskana.spi.task.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
import pro.taskana.task.api.models.Task;

public class ReviewRequiredManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRequiredManager.class);

  private final List<ReviewRequiredProvider> reviewRequiredProviders;

  public ReviewRequiredManager(TaskanaEngine taskanaEngine) {
    reviewRequiredProviders = SpiLoader.load(ReviewRequiredProvider.class);
    for (ReviewRequiredProvider serviceProvider : reviewRequiredProviders) {
      serviceProvider.initialize(taskanaEngine);
      LOGGER.info(
          "Registered ReviewRequiredProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (reviewRequiredProviders.isEmpty()) {
      LOGGER.info(
          "No ReviewRequiredProvider service provider found. "
              + "Running without any ReviewRequiredProvider implementation.");
    }
  }

  public boolean reviewRequired(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to ReviewRequiredProvider service providers: {}", task);
    }

    return reviewRequiredProviders.stream()
        .anyMatch(serviceProvider -> serviceProvider.reviewRequired(task));
  }
}
