package pro.taskana.taskrouting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.Task;
import pro.taskana.TaskanaEngine;
import pro.taskana.taskrouting.api.TaskRoutingProvider;

/**
 * Loads TaskRoutingProvider SPI implementation(s) and passes requests to determine workbasketids to
 * them.
 */
public final class TaskRoutingManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskRoutingManager.class);
  private static TaskRoutingManager singleton;
  private boolean enabled = false;
  private List<TaskRoutingProvider> theTaskRoutingProviders = new ArrayList<>();
  private ServiceLoader<TaskRoutingProvider> serviceLoader;

  private TaskRoutingManager(TaskanaEngine taskanaEngine) {
    serviceLoader = ServiceLoader.load(TaskRoutingProvider.class);
    for (TaskRoutingProvider router : serviceLoader) {
      router.initialize(taskanaEngine);
      theTaskRoutingProviders.add(router);
      LOGGER.info("Registered TaskRouter provider: {}", router.getClass().getName());
    }

    if (theTaskRoutingProviders.isEmpty()) {
      LOGGER.info("No TaskRouter provider found. Running without Task Routing.");
    } else {
      enabled = true;
    }
  }

  public static synchronized TaskRoutingManager getInstance(TaskanaEngine taskanaEngine) {
    if (singleton == null) {
      singleton = new TaskRoutingManager(taskanaEngine);
    }
    return singleton;
  }

  public static boolean isTaskRoutingEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  /**
   * Determines a workbasket id for a given task. Algorithm: The task that needs a workbasket id is
   * passed to all registered TaskRoutingProviders. If they return no or more than one workbasketId,
   * null is returned, otherwise we return the workbasketId that was returned from the
   * TaskRoutingProviders.
   *
   * @param task the task for which a workbasketId is to be determined.
   * @return the id of the workbasket in which the task is to be created.
   */
  public String determineWorkbasketId(Task task) {
    LOGGER.debug(
        "entry to routeToWorkbasket. TaskRouterr is enabled {}, task = {}",
        isTaskRoutingEnabled(),
        task);
    String workbasketId = null;
    if (isTaskRoutingEnabled()) {
      // route to all TaskRoutingProviders
      // collect in a set to see whether different workbasket ids are returned
      Set<String> workbasketIds =
          theTaskRoutingProviders.stream()
              .map(rtr -> rtr.determineWorkbasketId(task))
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
      if (workbasketIds.isEmpty()) {
        LOGGER.error("No TaskRouter determined a workbasket for task {}.", task);
      } else if (workbasketIds.size() > 1) {
        LOGGER.error("The TaskRouters determined more than one workbasket for task {}", task);
      } else {
        workbasketId = workbasketIds.stream().findFirst().orElse(null);
      }
    }
    LOGGER.debug("exit from routeToWorkbasketId. Destination WorkbasketId = {}", workbasketId);
    return workbasketId;
  }
}
