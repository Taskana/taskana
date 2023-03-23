package pro.taskana.spi.priority.internal;

import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.models.TaskSummary;

public class PriorityServiceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PriorityServiceManager.class);
  private final List<PriorityServiceProvider> priorityServiceProviders;

  public PriorityServiceManager(TaskanaEngine taskanaEngine) {
    priorityServiceProviders = SpiLoader.load(PriorityServiceProvider.class);
    for (PriorityServiceProvider priorityProvider : priorityServiceProviders) {
      priorityProvider.initialize(taskanaEngine);
      LOGGER.info("Registered PriorityServiceProvider: {}", priorityProvider.getClass().getName());
    }
    if (priorityServiceProviders.isEmpty()) {
      LOGGER.info("No PriorityServiceProvider found. Running without PriorityServiceProvider.");
    }
  }

  public boolean isEnabled() {
    return !priorityServiceProviders.isEmpty();
  }

  public OptionalInt calculatePriorityOfTask(TaskSummary task) {
    if (task.isManualPriorityActive()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Skip using PriorityServiceProviders because the Task is prioritised manually: {}",
            task);
      }
      return OptionalInt.empty();
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending Task to PriorityServiceProviders: {}", task);
    }

    Set<OptionalInt> priorities =
        priorityServiceProviders.stream()
            .map(wrap(provider -> provider.calculatePriority(task)))
            .filter(OptionalInt::isPresent)
            .collect(Collectors.toSet());

    if (priorities.size() == 1) {
      return priorities.iterator().next();
    } else if (!priorities.isEmpty() && LOGGER.isErrorEnabled()) {
      LOGGER.error(
          "The PriorityServiceProviders determined more than one priority for Task {}.",
          LogSanitizer.stripLineBreakingChars(task));
    }

    return OptionalInt.empty();
  }
}
