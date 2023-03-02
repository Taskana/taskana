package pro.taskana.spi.task.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.AfterRequestChangesProvider;
import pro.taskana.task.api.models.Task;

@Slf4j
public class AfterRequestChangesManager {

  private final List<AfterRequestChangesProvider> afterRequestChangesProviders;

  public AfterRequestChangesManager(TaskanaEngine taskanaEngine) {
    afterRequestChangesProviders = SpiLoader.load(AfterRequestChangesProvider.class);
    for (AfterRequestChangesProvider serviceProvider : afterRequestChangesProviders) {
      serviceProvider.initialize(taskanaEngine);
      log.info(
          "Registered AfterRequestChangesProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (afterRequestChangesProviders.isEmpty()) {
      log.info(
          "No AfterRequestChangesProvider service provider found. "
              + "Running without any AfterRequestChangesProvider implementation.");
    }
  }

  public Task afterRequestChanges(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to AfterRequestChangesProvider service providers: {}", task);
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
