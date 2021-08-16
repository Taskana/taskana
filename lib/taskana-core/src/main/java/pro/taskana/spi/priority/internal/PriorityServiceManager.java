package pro.taskana.spi.priority.internal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.models.TaskSummary;

public class PriorityServiceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PriorityServiceManager.class);
  private static PriorityServiceManager singleton;
  private final ServiceLoader<PriorityServiceProvider> serviceLoader;
  private boolean enabled = false;

  private PriorityServiceManager() {
    serviceLoader = ServiceLoader.load(PriorityServiceProvider.class);
    for (PriorityServiceProvider priorityProvider : serviceLoader) {
      LOGGER.info("Registered PriorityServiceProvider: {}", priorityProvider.getClass().getName());
      enabled = true;
    }
    if (!enabled) {
      LOGGER.info("No PriorityServiceProvider found. Running without PriorityServiceProvider.");
    }
  }

  public static synchronized PriorityServiceManager getInstance() {
    if (singleton == null) {
      singleton = new PriorityServiceManager();
    }
    return singleton;
  }

  public static boolean isPriorityServiceEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  public Optional<Integer> calculatePriorityOfTask(TaskSummary task) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending task to PriorityServiceProviders: {}", task);
    }

    // ServiceLoader.stream() is only available in Java11.
    List<Integer> priorities =
        StreamSupport.stream(serviceLoader.spliterator(), false)
            .map(provider -> getPriorityByProvider(task, provider))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .distinct()
            .collect(Collectors.toList());

    if (priorities.size() <= 1) {
      return priorities.stream().findFirst();
    }

    if (LOGGER.isErrorEnabled()) {
      LOGGER.error(
          "The PriorityServiceProviders determined more than one priority for Task {}.",
          LogSanitizer.stripLineBreakingChars(task));
    }
    return Optional.empty();
  }

  private Optional<Integer> getPriorityByProvider(
      TaskSummary task, PriorityServiceProvider provider) {
    try {
      return provider.calculatePriority(task);
    } catch (Exception e) {
      throw new SystemException(
          String.format(
              "Caught exception while calculating priority of Task in provider %s.",
              provider.getClass().getName()),
          e);
    }
  }
}
