package pro.taskana.spi.task.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.BeforeRequestChangesProvider;
import pro.taskana.task.api.models.Task;

@Slf4j
public class BeforeRequestChangesManager {

  private final List<BeforeRequestChangesProvider> beforeRequestChangesProviders;

  public BeforeRequestChangesManager(TaskanaEngine taskanaEngine) {
    beforeRequestChangesProviders = SpiLoader.load(BeforeRequestChangesProvider.class);
    for (BeforeRequestChangesProvider serviceProvider : beforeRequestChangesProviders) {
      serviceProvider.initialize(taskanaEngine);
      log.info(
          "Registered BeforeRequestChangesProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (beforeRequestChangesProviders.isEmpty()) {
      log.info(
          "No BeforeRequestChangesProvider service provider found. "
              + "Running without any BeforeRequestChangesProvider implementation.");
    }
  }

  public Task beforeRequestChanges(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to BeforeRequestChangesProvider service providers: {}", task);
    }
    for (BeforeRequestChangesProvider serviceProvider : beforeRequestChangesProviders) {
      try {
        task = serviceProvider.beforeRequestChanges(task);
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
