package io.kadai.spi.task.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.ReviewRequiredProvider;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewRequiredManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRequiredManager.class);

  private final List<ReviewRequiredProvider> reviewRequiredProviders;

  public ReviewRequiredManager(KadaiEngine kadaiEngine) {
    reviewRequiredProviders = SpiLoader.load(ReviewRequiredProvider.class);
    for (ReviewRequiredProvider serviceProvider : reviewRequiredProviders) {
      serviceProvider.initialize(kadaiEngine);
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
