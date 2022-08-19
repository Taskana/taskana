package pro.taskana.spi.task.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.AfterRequestChangesProvider;
import pro.taskana.task.api.models.Task;

public class AfterRequestChangesManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(AfterRequestChangesManager.class);

  private final List<AfterRequestChangesProvider> afterRequestChangesProviders;

  public AfterRequestChangesManager(TaskanaEngine taskanaEngine) {
    afterRequestChangesProviders = SpiLoader.load(AfterRequestChangesProvider.class);
    for (AfterRequestChangesProvider serviceProvider : afterRequestChangesProviders) {
      serviceProvider.initialize(taskanaEngine);
      LOGGER.info(
          "Registered AfterRequestChangesProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (afterRequestChangesProviders.isEmpty()) {
      LOGGER.info(
          "No AfterRequestChangesProvider service provider found. "
              + "Running without any AfterRequestChangesProvider implementation.");
    }
  }

  public Task afterRequestChanges(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to AfterRequestChangesProvider service providers: {}", task);
    }
    for (AfterRequestChangesProvider serviceProvider : afterRequestChangesProviders) {
      try {
        task = serviceProvider.afterRequestChanges(task);
      } catch (Exception e) {
        throw new SystemException(
            String.format(
                "service provider '%s' threw an exception", serviceProvider.getClass().getName()),
            e);
      }
    }
    return task;
  }
}
