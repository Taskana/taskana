package pro.taskana.spi.task.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
import pro.taskana.task.api.models.Task;

public class ReviewRequiredManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRequiredManager.class);

  private final List<ReviewRequiredProvider> reviewRequiredProviders;

  public ReviewRequiredManager(TaskanaEngine taskanaEngine) {
    reviewRequiredProviders = SpiLoader.load(ReviewRequiredProvider.class);
    for (ReviewRequiredProvider serviceProvider : reviewRequiredProviders) {
      serviceProvider.initialize(taskanaEngine);
      LOGGER.info(
          "Registered ReviewRequiredProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (reviewRequiredProviders.isEmpty()) {
      LOGGER.info(
          "No ReviewRequiredProvider service provider found. "
              + "Running without any ReviewRequiredProvider implementation.");
    }
  }

  public boolean reviewRequired(Task task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to ReviewRequiredProvider service providers: {}", task);
    }

    return reviewRequiredProviders.stream()
        .anyMatch(serviceProvider -> serviceProvider.reviewRequired(task));
  }
}
