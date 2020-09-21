package acceptance.taskpreprocessing;

import pro.taskana.spi.taskpreprocessing.api.CreateTaskPreprocessor;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.Task;

public class TestCreateTaskPreprocessorProvider implements CreateTaskPreprocessor {

  @Override
  public void processTaskBeforeCreation(Task taskToProcess) {
    taskToProcess
        .setCustomAttribute(TaskCustomField.CUSTOM_1, "preprocessedCustomField");
  }
}
