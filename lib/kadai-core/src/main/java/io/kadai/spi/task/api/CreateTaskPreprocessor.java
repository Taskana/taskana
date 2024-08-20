package io.kadai.spi.task.api;

import io.kadai.task.api.models.Task;

/**
 * The CreateTaskPreprocessor allows to implement customized behaviour before the given {@linkplain
 * Task} has been created.
 */
public interface CreateTaskPreprocessor {

  /**
   * Perform any action before a {@linkplain Task} has been created through {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed within the same transaction staple as {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task)}.
   *
   * <p>This SPI is executed with the same {@linkplain io.kadai.common.api.security.UserPrincipal}
   * and {@linkplain io.kadai.common.api.security.GroupPrincipal} as in {@linkplain
   * io.kadai.task.api.TaskService#createTask(Task)}.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   */
  void processTaskBeforeCreation(Task taskToProcess);
}
