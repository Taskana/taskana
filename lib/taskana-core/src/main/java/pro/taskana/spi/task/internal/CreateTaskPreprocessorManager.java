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

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.models.Task;

public class CreateTaskPreprocessorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTaskPreprocessorManager.class);
  private final List<CreateTaskPreprocessor> createTaskPreprocessors;

  public CreateTaskPreprocessorManager() {
    createTaskPreprocessors = SpiLoader.load(CreateTaskPreprocessor.class);
    for (CreateTaskPreprocessor preprocessor : createTaskPreprocessors) {
      LOGGER.info(
          "Registered CreateTaskPreprocessor provider: {}", preprocessor.getClass().getName());
    }
    if (createTaskPreprocessors.isEmpty()) {
      LOGGER.info("No CreateTaskPreprocessor found. Running without CreateTaskPreprocessor.");
    }
  }

  public Task processTaskBeforeCreation(Task taskToProcess) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending task to CreateTaskPreprocessor providers: {}", taskToProcess);
    }
    createTaskPreprocessors.forEach(
        wrap(
            createTaskPreprocessor ->
                createTaskPreprocessor.processTaskBeforeCreation(taskToProcess)));
    return taskToProcess;
  }

  public boolean isEnabled() {
    return !createTaskPreprocessors.isEmpty();
  }
}
