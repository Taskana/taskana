package pro.taskana.spi.task.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.BeforeRequestReviewProvider;
import pro.taskana.task.api.models.Task;

@Slf4j
public class BeforeRequestReviewManager {

  private final List<BeforeRequestReviewProvider> beforeRequestReviewProviders;

  public BeforeRequestReviewManager(TaskanaEngine taskanaEngine) {
    beforeRequestReviewProviders = SpiLoader.load(BeforeRequestReviewProvider.class);
    for (BeforeRequestReviewProvider serviceProvider : beforeRequestReviewProviders) {
      serviceProvider.initialize(taskanaEngine);
      log.info(
          "Registered BeforeRequestReviewProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (beforeRequestReviewProviders.isEmpty()) {
      log.info(
          "No BeforeRequestReviewProvider service provider found. "
              + "Running without any BeforeRequestReviewProvider implementation.");
    }
  }

  public Task beforeRequestReview(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to BeforeRequestReviewProvider service providers: {}", task);
    }
    for (BeforeRequestReviewProvider serviceProvider : beforeRequestReviewProviders) {
      try {
        task = serviceProvider.beforeRequestReview(task);
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
