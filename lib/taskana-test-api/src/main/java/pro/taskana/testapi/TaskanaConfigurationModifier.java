package pro.taskana.testapi;

import pro.taskana.TaskanaConfiguration;

public interface TaskanaConfigurationModifier {

  TaskanaConfiguration.Builder modify(TaskanaConfiguration.Builder builder);
}
