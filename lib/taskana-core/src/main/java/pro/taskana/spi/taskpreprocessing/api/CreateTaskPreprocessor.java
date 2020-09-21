package pro.taskana.spi.taskpreprocessing.api;

import pro.taskana.task.api.models.Task;

public interface CreateTaskPreprocessor {

  /**
   * Processes a task before its creation.
   *
   * @param taskToProcess {@link Task} The Task to preprocess.
   */
  void processTaskBeforeCreation(Task taskToProcess);
}
