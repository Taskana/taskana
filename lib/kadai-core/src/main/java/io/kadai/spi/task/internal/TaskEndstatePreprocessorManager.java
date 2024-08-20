package io.kadai.spi.task.internal;

import static io.kadai.common.internal.util.CheckedConsumer.wrap;

import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.TaskEndstatePreprocessor;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskEndstatePreprocessorManager {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskEndstatePreprocessorManager.class);
  private final List<TaskEndstatePreprocessor> taskEndstatePreprocessors;

  public TaskEndstatePreprocessorManager() {
    taskEndstatePreprocessors = SpiLoader.load(TaskEndstatePreprocessor.class);
    for (TaskEndstatePreprocessor preprocessor : taskEndstatePreprocessors) {
      LOGGER.info(
          "Registered TaskEndstatePreprocessor provider: {}", preprocessor.getClass().getName());
    }
    if (taskEndstatePreprocessors.isEmpty()) {
      LOGGER.info("No TaskEndstatePreprocessor found. Running without TaskEndstatePreprocessor.");
    }
  }

  public Task processTaskBeforeEndstate(Task taskToProcess) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending task to TaskEndstatePreprocessor providers: {}", taskToProcess);
    }
    taskEndstatePreprocessors.forEach(
        wrap(
            taskEndstatePreprocessor ->
                taskEndstatePreprocessor.processTaskBeforeEndstate(taskToProcess)));
    return taskToProcess;
  }
}
