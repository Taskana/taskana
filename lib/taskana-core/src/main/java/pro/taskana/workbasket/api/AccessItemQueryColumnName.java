package pro.taskana.workbasket.api;

import pro.taskana.common.api.QueryColumnName;
import pro.taskana.workbasket.internal.WorkbasketQueryMapper;

/**
 * Enum containing the column names for {@link
 * WorkbasketQueryMapper#queryWorkbasketAccessItemColumnValues}.
 *
 * @author jsa
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
