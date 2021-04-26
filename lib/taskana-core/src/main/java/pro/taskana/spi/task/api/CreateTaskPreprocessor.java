package pro.taskana.spi.task.api;

import pro.taskana.task.api.models.Task;

public interface CreateTaskPreprocessor {

  /**
   * Processes a {@linkplain Task} before its creation.
   *
   * @param taskToProcess the {@linkplain Task} to preprocess
   */
  void processTaskBeforeCreation(Task taskToProcess);
}
