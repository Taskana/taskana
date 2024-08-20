package io.kadai.testapi.util;

import io.kadai.spi.task.api.CreateTaskPreprocessor;
import io.kadai.task.api.models.Task;

public class TopLevelCreateTaskPreprocessor implements CreateTaskPreprocessor {

  @Override
  public void processTaskBeforeCreation(Task taskToProcess) {
    // implementation not important for the tests
  }
}
