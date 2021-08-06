package pro.taskana.workbasket.api;

import java.util.Arrays;

import pro.taskana.common.api.QueryColumnName;

/**
 * Enum containing the column names for
 * pro.taskana.workbasket.internal.WorkbasketQueryMapper.queryWorkbasketColumnValues.
 */
public enum WorkbasketQueryColumnName implements QueryColumnName {
  OWNER("w.owner"),
  ID("w.id"),
  KEY("w.key"),
  NAME("w.name"),
  DOMAIN("w.domain"),
  TYPE("w.type"),
  CUSTOM_1("w.custom_1"),
  CUSTOM_2("w.custom_2"),
  CUSTOM_3("w.custom_3"),
  CUSTOM_4("w.custom_4"),
  ORG_LEVEL_1("w.org_level_1"),
  ORG_LEVEL_2("w.org_level_2"),
  ORG_LEVEL_3("w.org_level_3"),
  ORG_LEVEL_4("w.org_level_4");

  private String name;

  WorkbasketQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
