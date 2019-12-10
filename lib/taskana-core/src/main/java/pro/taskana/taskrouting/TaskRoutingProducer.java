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
import pro.taskana.taskrouting.api.TaskRouter;

/**
 * Loads TaskRouter SPI implementation(s) and passes requests route tasks to workbaskets to the router(s).
 */
public final class TaskRoutingProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRoutingProducer.class);
    private static TaskRoutingProducer singleton;
    private static boolean enabled = false;
    private ServiceLoader<TaskRouter> serviceLoader;
    private static List<TaskRouter> theTaskRouters = new ArrayList<>();

    private TaskRoutingProducer(TaskanaEngine taskanaEngine) {
        serviceLoader = ServiceLoader.load(TaskRouter.class);
        for (TaskRouter router : serviceLoader) {
            router.initialize(taskanaEngine);
            theTaskRouters.add(router);
            LOGGER.info("Registered TaskRouter provider: {}", router.getClass().getName());
        }

        if (theTaskRouters.isEmpty()) {
            LOGGER.info("No TaskRouter provider found. Running without Task Routing.");
        } else {
            enabled = true;
        }
    }

    public static synchronized TaskRoutingProducer getInstance(TaskanaEngine taskanaEngine) {
        if (singleton == null) {
            singleton = new TaskRoutingProducer(taskanaEngine);
        }
        return singleton;
    }

    public static boolean isTaskRoutingEnabled() {
        return enabled;
    }

    /**
     * routes tasks to Workbaskets.
     * The task that is to be routed is passed to all registered TaskRouters. If they return no or more than one
     * workbasketId, null is returned, otherwise we return the workbasketId that was returned from the TaskRouters.
     * @param task  the task for which a workbasketId is to be determined.
     * @return the id of the workbasket in which the task is to be created.
     */
    public String routeToWorkbasketId(Task task) {
             LOGGER.debug("entry to routeToWorkbasket. TaskRouterr is enabled {}, task = {}", isTaskRoutingEnabled(), task);
             String workbasketId = null;
             if (isTaskRoutingEnabled()) {
                 // route to all task routers
                 // collect in a set to see whether different workbasket ids are returned
                 Set<String> workbasketIds = theTaskRouters.stream()
                               .map(rtr -> rtr.routeToWorkbasketId(task))
                               .filter(Objects::nonNull)
                               .collect(Collectors.toSet());
                 if (workbasketIds.isEmpty()) {
                     LOGGER.error("No TaskRouter determined a workbasket for task {}.", task);
                 } else if (workbasketIds.size() > 1) {
                     LOGGER.error("The TaskRouters determined more than one workbasket for task{}", task);
                 } else {
                     workbasketId = workbasketIds.stream().findFirst().orElse(null);
                 }
             }
             LOGGER.debug("exit from routeToWorkbasketId. Destination WorkbasketId = {}", workbasketId);
             return workbasketId;
     }
}
