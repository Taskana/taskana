package pro.taskana.task.api;

import pro.taskana.common.api.TaskanaEngine;

/** Interface for TASKANA TaskRoutingProvider SPI. */
public interface TaskRoutingProvider {

  /**
   * Initialize TaskRoutingProvider service.
   *
   * @param taskanaEngine {@link TaskanaEngine} The Taskana engine needed for initialization.
   */
  void initialize(TaskanaEngine taskanaEngine);

  /**
   * Determines a WorkbasketId for a given task.
   *
   * @param task {@link Task} The task for which a workbasket must be determined.
   * @return the id of the workbasket in which the task is to be created.
   */
  String determineWorkbasketId(Task task);
}
