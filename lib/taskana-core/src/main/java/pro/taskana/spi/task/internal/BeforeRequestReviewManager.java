package pro.taskana.spi.task.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.BeforeRequestReviewProvider;
import pro.taskana.task.api.models.Task;

public class BeforeRequestReviewManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeforeRequestReviewManager.class);

  private final List<BeforeRequestReviewProvider> beforeRequestReviewProviders;

  public BeforeRequestReviewManager(TaskanaEngine taskanaEngine) {
    beforeRequestReviewProviders = SpiLoader.load(BeforeRequestReviewProvider.class);
    for (BeforeRequestReviewProvider serviceProvider : beforeRequestReviewProviders) {
      serviceProvider.initialize(taskanaEngine);
      LOGGER.info(
          "Registered BeforeRequestReviewProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (beforeRequestReviewProviders.isEmpty()) {
      LOGGER.info(
          "No BeforeRequestReviewProvider service provider found. "
              + "Running without any BeforeRequestReviewProvider implementation.");
    }
  }

  public Task beforeRequestReview(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to BeforeRequestReview service providers: {}", task);
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
