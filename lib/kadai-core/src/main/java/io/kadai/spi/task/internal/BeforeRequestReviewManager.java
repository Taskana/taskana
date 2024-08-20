package io.kadai.spi.task.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.BeforeRequestReviewProvider;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeRequestReviewManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeforeRequestReviewManager.class);

  private final List<BeforeRequestReviewProvider> beforeRequestReviewProviders;

  public BeforeRequestReviewManager(KadaiEngine kadaiEngine) {
    beforeRequestReviewProviders = SpiLoader.load(BeforeRequestReviewProvider.class);
    for (BeforeRequestReviewProvider serviceProvider : beforeRequestReviewProviders) {
      serviceProvider.initialize(kadaiEngine);
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
      LOGGER.debug("Sending Task to BeforeRequestReviewProvider service providers: {}", task);
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
