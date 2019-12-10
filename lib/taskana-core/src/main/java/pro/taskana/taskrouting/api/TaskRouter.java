package pro.taskana.taskrouting.api;

import pro.taskana.Task;
import pro.taskana.TaskanaEngine;

/**
 * Interface for TASKANA TaskRouter SPI.
 */
public interface TaskRouter {

    /**
     * Initialize TaskRouter service.
     *
     * @param  taskanaEngine
     *            {@link TaskanaEngine} The Taskana engine for needed initialization.
     */
    void initialize(TaskanaEngine taskanaEngine);

    /**
     * Determines a WorkbasketId for a given task.
     *
     * @param task
     *            {@link Task} The task for which a workbasket must be determined.
     * @return the id of the workbasket in which the task is to be created.
     */
    String routeToWorkbasketId(Task task);

}
