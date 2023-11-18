package pro.taskana.spi.task.api;

import pro.taskana.task.api.models.Task;

/**
 * The TaskEndstatePreprocessor allows to implement customized behaviour before the given
 * {@linkplain Task} goes into an {@linkplain pro.taskana.task.api.TaskState#END_STATES end state}
 * (cancelled, terminated or completed).
 */
public interface TaskEndstatePreprocessor {

  /**
   * Perform any action before a {@linkplain Task} goes into an {@linkplain
   * pro.taskana.task.api.TaskState#END_STATES end state}. A {@linkplain Task} goes into an end
   * state at the end of the following methods: {@linkplain
   * pro.taskana.task.api.TaskService#completeTask(String)}, {@linkplain
   * pro.taskana.task.api.TaskService#cancelTask(String)}, {@linkplain
   * pro.taskana.task.api.TaskService#terminateTask(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#completeTask(String)}, {@linkplain
   * pro.taskana.task.api.TaskService#cancelTask(String)}, {@linkplain
   * pro.taskana.task.api.TaskService#terminateTask(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in the methods mentioned above.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   */
  Task processTaskBeforeEndstate(Task taskToProcess);
}
