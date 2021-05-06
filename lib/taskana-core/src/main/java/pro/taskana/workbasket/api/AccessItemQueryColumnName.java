package pro.taskana.workbasket.api;

import pro.taskana.common.api.QueryColumnName;

/**
 * This Enum contains the database column names for a {@linkplain
 * pro.taskana.workbasket.api.models.WorkbasketAccessItem WorkbasketAccessItem}.
 */
public enum AccessItemQueryColumnName implements QueryColumnName {
  ID("id"),
  WORKBASKET_ID("workbasket_id"),
  WORKBASKET_KEY("wb.key"),
  ACCESS_ID("access_id");

  private String name;

  AccessItemQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
