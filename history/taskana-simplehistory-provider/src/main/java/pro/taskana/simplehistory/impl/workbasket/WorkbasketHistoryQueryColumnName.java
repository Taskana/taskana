package pro.taskana.simplehistory.impl.workbasket;

import pro.taskana.common.api.QueryColumnName;

/** Enum containing the column names for {@link WorkbasketHistoryQueryMapper}. */
public enum WorkbasketHistoryQueryColumnName implements QueryColumnName {
  ID("id"),
  WORKBASKET_ID("workbasket_id"),
  EVENT_TYPE("event_type"),
  CREATED("created"),
  USER_ID("user_id"),
  DOMAIN("domain"),
  WORKBASKET_KEY("workbasket_key"),
  WORKBASKET_TYPE("workbasket_type"),
  OWNER("owner"),
  CUSTOM_1("custom_1"),
  CUSTOM_2("custom_2"),
  CUSTOM_3("custom_3"),
  CUSTOM_4("custom_4"),
  ORGLEVEL_1("orgLevel_1"),
  ORGLEVEL_2("orgLevel_2"),
  ORGLEVEL_3("orgLevel_3"),
  ORGLEVEL_4("orgLevel_4");

  private String name;

  WorkbasketHistoryQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
