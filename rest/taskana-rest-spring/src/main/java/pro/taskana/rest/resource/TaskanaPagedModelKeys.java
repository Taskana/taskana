package pro.taskana.rest.resource;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TaskanaPagedModelKeys {
  ACCESSITEMS("accessItems"),
  CLASSIFICATIONS("classifications"),
  DISTRIBUTION_TARGETS("distributionTargets"),
  TASKS("tasks"),
  TASK_COMMENTS("taskComments"),
  WORKBASKETS("workbaskets");

  private static final Map<String, TaskanaPagedModelKeys> PROPERTY_MAP =
      Arrays.stream(TaskanaPagedModelKeys.values())
          .collect(Collectors.toMap(TaskanaPagedModelKeys::getPropertyName, Function.identity()));

  private final String propertyName;

  TaskanaPagedModelKeys(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public static Optional<TaskanaPagedModelKeys> getEnumFromPropertyName(String propertyName) {
    return Optional.ofNullable(PROPERTY_MAP.get(propertyName));
  }
}
