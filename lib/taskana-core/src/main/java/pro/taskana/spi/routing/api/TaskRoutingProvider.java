package pro.taskana.spi.routing.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/** Interface for TASKANA TaskRoutingProvider SPI. */
public interface TaskRoutingProvider {

  /**
   * Initialize TaskRoutingProvider service.
   *
   * @param taskanaEngine The {@link TaskanaEngine} needed for initialization.
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Determines a workbasketId for a given {@linkplain Task} in a given domain.
   *
   * @param domain The domain in which the {@linkplain Task} is to be routed
   * @param task The {@linkplain Task} for which a Workbasket must be determined
   * @return the id of the Workbasket in which the {@linkplain Task} is to be created
   */
  String determineWorkbasketId(String domain, Task task);
}
