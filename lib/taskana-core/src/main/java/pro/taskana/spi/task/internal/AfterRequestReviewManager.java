package pro.taskana.spi.task.internal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.task.api.AfterRequestReviewProvider;
import pro.taskana.task.api.models.Task;

@Slf4j
public class AfterRequestReviewManager {

  private final List<AfterRequestReviewProvider> afterRequestReviewProviders;

  public AfterRequestReviewManager(TaskanaEngine taskanaEngine) {
    afterRequestReviewProviders = SpiLoader.load(AfterRequestReviewProvider.class);
    for (AfterRequestReviewProvider serviceProvider : afterRequestReviewProviders) {
      serviceProvider.initialize(taskanaEngine);
      log.info(
          "Registered AfterRequestReviewProvider service provider: {}",
          serviceProvider.getClass().getName());
    }
    if (afterRequestReviewProviders.isEmpty()) {
      log.info(
          "No AfterRequestReviewProvider service provider found. "
              + "Running without any AfterRequestReviewProvider implementation.");
    }
  }

  public Task afterRequestReview(Task task) {
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to AfterRequestReviewProvider service providers: {}", task);
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
