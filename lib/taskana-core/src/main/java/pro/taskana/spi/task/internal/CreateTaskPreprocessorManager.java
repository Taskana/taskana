package pro.taskana.spi.task.internal;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.models.Task;

@Slf4j
public class CreateTaskPreprocessorManager {

  private final List<CreateTaskPreprocessor> createTaskPreprocessors;

  public CreateTaskPreprocessorManager() {
    createTaskPreprocessors = SpiLoader.load(CreateTaskPreprocessor.class);
    for (CreateTaskPreprocessor preprocessor : createTaskPreprocessors) {
      log.info("Registered CreateTaskPreprocessor provider: {}", preprocessor.getClass().getName());
    }
    if (createTaskPreprocessors.isEmpty()) {
      log.info("No CreateTaskPreprocessor found. Running without CreateTaskPreprocessor.");
    }
  }

  public Task processTaskBeforeCreation(Task taskToProcess) {
    if (log.isDebugEnabled()) {
      log.debug("Sending task to CreateTaskPreprocessor providers: {}", taskToProcess);
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
