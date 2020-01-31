package pro.taskana.task.api;

import pro.taskana.common.api.QueryColumnName;
import pro.taskana.task.internal.TaskQueryMapper;

/**
 * Enum containing the column names for {@link TaskQueryMapper#queryObjectReferenceColumnValues}.
 *
 * @author jsa
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
