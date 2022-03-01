package pro.taskana.spi.routing.internal;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.CheckedFunction;
import pro.taskana.common.internal.util.LogSanitizer;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

/**
 * Loads {@link TaskRoutingProvider} SPI implementation(s) and passes requests to determine
 * workbasketIds to them.
 */
public final class TaskRoutingManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskRoutingManager.class);
  private final List<TaskRoutingProvider> taskRoutingProviders;

  public TaskRoutingManager(TaskanaEngine taskanaEngine) {
    taskRoutingProviders = SpiLoader.load(TaskRoutingProvider.class);
    for (TaskRoutingProvider taskRoutingProvider : taskRoutingProviders) {
      taskRoutingProvider.initialize(taskanaEngine);
      LOGGER.info("Registered TaskRouter provider: {}", taskRoutingProvider.getClass().getName());
    }

    if (taskRoutingProviders.isEmpty()) {
      LOGGER.info("No TaskRouter provider found. Running without Task routing.");
    }
  }

  /**
   * Determines a workbasketId for a given {@linkplain Task}.
   *
   * <p>Algorithm: The Task that needs a workbasketId is passed to all registered
   * TaskRoutingProviders. If they return no or more than one workbasketId, null is returned,
   * otherwise we return the workbasketId that was returned from the TaskRoutingProviders.
   *
   * @param domain The domain in which the {@linkplain Task} is to be routed
   * @param task The {@linkplain Task} for which a Workbasket must be determined
   * @return the id of the Workbasket in which the {@linkplain Task} is to be created
   */
  public String determineWorkbasketId(String domain, Task task) {
    String workbasketId = null;
    if (isEnabled()) {
      Set<String> workbasketIds =
          taskRoutingProviders.stream()
              .map(
                  CheckedFunction.wrap(
                      taskRoutingProvider ->
                          taskRoutingProvider.determineWorkbasketId(domain, task)))
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
      if (workbasketIds.isEmpty()) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(
              "No TaskRouter determined a workbasket for task {}.",
              LogSanitizer.stripLineBreakingChars(task));
        }
      } else if (workbasketIds.size() > 1) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(
              "The TaskRouters determined more than one workbasket for task {}",
              LogSanitizer.stripLineBreakingChars(task));
        }
      } else {
        workbasketId = workbasketIds.iterator().next();
      }
    }
    return workbasketId;
  }

  public boolean isEnabled() {
    return !taskRoutingProviders.isEmpty();
  }
}
