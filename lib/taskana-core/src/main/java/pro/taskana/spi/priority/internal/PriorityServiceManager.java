package pro.taskana.spi.priority.internal;

import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.models.TaskSummary;

@Slf4j
public class PriorityServiceManager {

  private final List<PriorityServiceProvider> priorityServiceProviders;

  public PriorityServiceManager() {
    priorityServiceProviders = SpiLoader.load(PriorityServiceProvider.class);
    for (PriorityServiceProvider priorityProvider : priorityServiceProviders) {
      log.info("Registered PriorityServiceProvider: {}", priorityProvider.getClass().getName());
    }
    if (priorityServiceProviders.isEmpty()) {
      log.info("No PriorityServiceProvider found. Running without PriorityServiceProvider.");
    }
  }

  public boolean isEnabled() {
    return !priorityServiceProviders.isEmpty();
  }

  public OptionalInt calculatePriorityOfTask(TaskSummary task) {
    if (task.isManualPriorityActive()) {
      if (log.isDebugEnabled()) {
        log.debug(
            "Skip using PriorityServiceProviders because the Task is prioritised manually: {}",
            task);
      }
      return OptionalInt.empty();
    }
    if (log.isDebugEnabled()) {
      log.debug("Sending Task to PriorityServiceProviders: {}", task);
    }

    Set<OptionalInt> priorities =
        priorityServiceProviders.stream()
            .map(wrap(provider -> provider.calculatePriority(task)))
            .filter(OptionalInt::isPresent)
            .collect(Collectors.toSet());

    if (priorities.size() == 1) {
      return priorities.iterator().next();
    } else if (!priorities.isEmpty() && log.isErrorEnabled()) {
      log.error(
          "The PriorityServiceProviders determined more than one priority for Task {}.",
          LogSanitizer.stripLineBreakingChars(task));
    }

    return OptionalInt.empty();
  }
}
