package io.kadai.spi.task.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.task.api.AfterRequestReviewProvider;
import io.kadai.task.api.models.Task;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterRequestReviewManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(AfterRequestReviewManager.class);

  private final List<AfterRequestReviewProvider> afterRequestReviewProviders;

  public AfterRequestReviewManager(KadaiEngine kadaiEngine) {
    afterRequestReviewProviders = SpiLoader.load(AfterRequestReviewProvider.class);
    for (AfterRequestReviewProvider serviceProvider : afterRequestReviewProviders) {
      serviceProvider.initialize(kadaiEngine);
      LOGGER.info(
          "Registered AfterRequestReviewProvider service provider: {}",
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
      LOGGER.debug("Sending Task to AfterRequestReviewProvider service providers: {}", task);
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
