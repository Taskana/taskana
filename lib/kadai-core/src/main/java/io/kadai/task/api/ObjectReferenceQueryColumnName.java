package io.kadai.task.api;

import io.kadai.common.api.QueryColumnName;

/**
 * Enum containing the column names for {@link
 * io.kadai.task.internal.ObjectReferenceMapper#queryObjectReferenceColumnValues}.
 */
public enum ObjectReferenceQueryColumnName implements QueryColumnName {
  ID("id"),
  COMPANY("company"),
  SYSTEM("system"),
  SYSTEM_INSTANCE("system_instance"),
  TYPE("type"),
  VALUE("value");

  private final String name;

  ObjectReferenceQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
