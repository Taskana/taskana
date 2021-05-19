package pro.taskana.spi.routing.api;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.models.Task;

/** Interface for TASKANA TaskRoutingProvider SPI. */
public interface TaskRoutingProvider {

  /**
   * Initializes TaskRoutingProvider service.
   *
   * @param taskanaEngine The {@linkplain TaskanaEngine} needed for initialization
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Determines a workbasketId for a given {@linkplain Task}.
   *
   * @param task the {@linkplain Task} for which a {@linkplain
   *     pro.taskana.workbasket.api.models.Workbasket Workbasket} must be determined
   * @return the id of the {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket} in
   *     which the {@linkplain Task} is to be created
   */
  String determineWorkbasketId(Task task);
}
