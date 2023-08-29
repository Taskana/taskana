package pro.taskana.spi.task.internal;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.TaskEndstatePreprocessor;
import pro.taskana.task.api.models.Task;

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
