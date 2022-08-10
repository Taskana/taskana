package pro.taskana.spi.task.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.AfterRequestReviewProvider;
import pro.taskana.task.api.models.Task;

public class AfterRequestReviewManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(AfterRequestReviewManager.class);

  private final List<AfterRequestReviewProvider> afterRequestReviewProviders;

  public AfterRequestReviewManager(TaskanaEngine taskanaEngine) {
    afterRequestReviewProviders = SpiLoader.load(AfterRequestReviewProvider.class);
    for (AfterRequestReviewProvider serviceProvider : afterRequestReviewProviders) {
      serviceProvider.initialize(taskanaEngine);
      LOGGER.info(
          "Registered AfterRequestReview service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (afterRequestReviewProviders.isEmpty()) {
      LOGGER.info(
          "No AfterRequestReviewProvider service provider found. "
              + "Running without any AfterRequestReviewProvider implementation.");
    }
  }

  public Task afterRequestReview(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to AfterRequestReview service providers: {}", task);
    }
    for (AfterRequestReviewProvider serviceProvider : afterRequestReviewProviders) {
      try {
        task = serviceProvider.afterRequestReview(task);
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
