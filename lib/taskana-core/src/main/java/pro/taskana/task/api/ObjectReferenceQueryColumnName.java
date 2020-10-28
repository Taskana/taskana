package pro.taskana.task.api;

import pro.taskana.common.api.QueryColumnName;

/**
 * Enum containing the column names for {@link
 * pro.taskana.task.internal.TaskQueryMapper#queryObjectReferenceColumnValues}.
 */
public enum ObjectReferenceQueryColumnName implements QueryColumnName {
  ID("id"),
  COMPANY("company"),
  SYSTEM("system"),
  SYSTEM_INSTANCE("system_instance"),
  TYPE("type"),
  VALUE("value");

  private String name;

  ObjectReferenceQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
