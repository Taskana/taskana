package pro.taskana.spi.task.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
import pro.taskana.task.api.models.Task;

@Slf4j
public class ReviewRequiredManager {

  private final List<ReviewRequiredProvider> reviewRequiredProviders;

  public ReviewRequiredManager(TaskanaEngine taskanaEngine) {
    reviewRequiredProviders = SpiLoader.load(ReviewRequiredProvider.class);
    for (ReviewRequiredProvider serviceProvider : reviewRequiredProviders) {
      serviceProvider.initialize(taskanaEngine);
      log.info(
          "Registered ReviewRequiredProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (reviewRequiredProviders.isEmpty()) {
      log.info(
          "No ReviewRequiredProvider service provider found. "
              + "Running without any ReviewRequiredProvider implementation.");
    }
  }

  public boolean reviewRequired(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to ReviewRequiredProvider service providers: {}", task);
    }

    return reviewRequiredProviders.stream()
        .anyMatch(serviceProvider -> serviceProvider.reviewRequired(task));
  }
}
