package io.kadai.spi.task.api;

import io.kadai.task.api.models.Task;

/**
 * The TaskEndstatePreprocessor allows to implement customized behaviour before the given
 * {@linkplain Task} goes into an {@linkplain io.kadai.task.api.TaskState#END_STATES end state}
 * (cancelled, terminated or completed).
 */
public interface TaskEndstatePreprocessor {

  /**
   * Perform any action before a {@linkplain Task} goes into an {@linkplain
   * io.kadai.task.api.TaskState#END_STATES end state}. A {@linkplain Task} goes into an end state
   * at the end of the following methods: {@linkplain
   * io.kadai.task.api.TaskService#completeTask(String)}, {@linkplain
   * io.kadai.task.api.TaskService#cancelTask(String)}, {@linkplain
   * io.kadai.task.api.TaskService#terminateTask(String)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * io.kadai.task.api.TaskService#completeTask(String)}, {@linkplain
   * io.kadai.task.api.TaskService#cancelTask(String)}, {@linkplain
   * io.kadai.task.api.TaskService#terminateTask(String)}.
   *
   * <p>This SPI is executed with the same {@linkplain io.kadai.common.api.security.UserPrincipal}
   * and {@linkplain io.kadai.common.api.security.GroupPrincipal} as in the methods mentioned above.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   * @return the modified {@linkplain Task}. <b>IMPORTANT:</b> persistent changes to the {@linkplain
   *     Task} have to be managed by the service provider
   */
  Task processTaskBeforeEndstate(Task taskToProcess);
}
