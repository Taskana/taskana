package pro.taskana.spi.task.internal;

import java.util.Objects;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.models.Task;

public class CreateTaskPreprocessorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTaskPreprocessorManager.class);
  private static CreateTaskPreprocessorManager singleton;
  private final ServiceLoader<CreateTaskPreprocessor> serviceLoader;
  private boolean enabled = false;

  private CreateTaskPreprocessorManager() {
    serviceLoader = ServiceLoader.load(CreateTaskPreprocessor.class);
    for (CreateTaskPreprocessor preprocessor : serviceLoader) {
      LOGGER.info(
          "Registered CreateTaskPreprocessor provider: {}", preprocessor.getClass().getName());
      enabled = true;
    }
    if (!enabled) {
      LOGGER.info("No CreateTaskPreprocessor found. Running without CreateTaskPreprocessor.");
    }
  }

  public static synchronized CreateTaskPreprocessorManager getInstance() {
    if (singleton == null) {
      singleton = new CreateTaskPreprocessorManager();
    }
    return singleton;
  }

  public static boolean isCreateTaskPreprocessorEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  public Task processTaskBeforeCreation(Task taskToProcess) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending task to CreateTaskPreprocessor providers: {}", taskToProcess);
    }
    serviceLoader.forEach(
        createTaskPreprocessorProvider -> {
          try {
            createTaskPreprocessorProvider.processTaskBeforeCreation(taskToProcess);
          } catch (Exception e) {
            LOGGER.error(
                String.format(
                    "Caught exception while processing task before creation in class %s",
                    createTaskPreprocessorProvider.getClass().getName()),
                e);
            throw new SystemException(e.getMessage(), e.getCause());
          }
        });
    return taskToProcess;
  }
}
