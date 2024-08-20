package io.kadai.spi.task.internal;

import static io.kadai.common.internal.util.CheckedConsumer.wrap;

import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.CreateTaskPreprocessor;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
