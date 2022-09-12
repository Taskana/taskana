package pro.taskana.spi.task.api;

import pro.taskana.task.api.models.Task;

/**
 * The CreateTaskPreprocessor allows to implement customized behaviour before the given {@linkplain
 * Task} has been created.
 */
public interface CreateTaskPreprocessor {

  /**
   * Perform any action before a {@linkplain Task} has been created through {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed with the same {@linkplain
   * pro.taskana.common.api.security.UserPrincipal} and {@linkplain
   * pro.taskana.common.api.security.GroupPrincipal} as in {@linkplain
   * pro.taskana.task.api.TaskService#createTask(Task)}.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   */
  void processTaskBeforeCreation(Task taskToProcess);
}
