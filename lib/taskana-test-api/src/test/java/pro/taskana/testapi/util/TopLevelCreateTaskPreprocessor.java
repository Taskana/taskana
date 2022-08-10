package pro.taskana.testapi.util;

import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.models.Task;

public class TopLevelCreateTaskPreprocessor implements CreateTaskPreprocessor {

  @Override
  public void processTaskBeforeCreation(Task taskToProcess) {
    // implementation not important for the tests
  }
}
