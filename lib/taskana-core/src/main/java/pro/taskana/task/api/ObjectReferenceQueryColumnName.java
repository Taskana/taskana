package pro.taskana.task.api;

import pro.taskana.common.api.QueryColumnName;

/**
 * This Enum contains the database column names for a {@linkplain
 * pro.taskana.task.api.models.ObjectReference ObjectReference}.
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
