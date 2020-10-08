package pro.taskana.common.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.api.exceptions.SystemException;

/** This enum contains all roles that are known to taskana. */
public enum TaskanaRole {
  USER("taskana.roles.user"),
  BUSINESS_ADMIN("taskana.roles.businessadmin"),
  ADMIN("taskana.roles.admin"),
  MONITOR("taskana.roles.monitor"),
  TASK_ADMIN("taskana.roles.taskadmin");

  private final String propertyName;

  TaskanaRole(String propertyName) {
    this.propertyName = propertyName;
  }

  public static TaskanaRole fromPropertyName(String name) {
    return Arrays.stream(TaskanaRole.values())
        .filter(x -> x.propertyName.equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(
            () ->
                new SystemException("Internal System error when processing role property " + name));
  }

  public static List<String> getValidPropertyNames() {
    return Arrays.stream(values()).map(TaskanaRole::getPropertyName).collect(Collectors.toList());
  }

  public String getPropertyName() {
    return propertyName;
  }
}
