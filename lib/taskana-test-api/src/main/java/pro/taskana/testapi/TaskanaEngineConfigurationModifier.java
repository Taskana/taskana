package pro.taskana.testapi;

import pro.taskana.TaskanaConfiguration;

public interface TaskanaEngineConfigurationModifier {

  TaskanaConfiguration.Builder modify(
      TaskanaConfiguration.Builder taskanaEngineConfigurationBuilder);
}
