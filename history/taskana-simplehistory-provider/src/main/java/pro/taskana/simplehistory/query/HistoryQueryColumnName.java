package pro.taskana.simplehistory.query;

import pro.taskana.common.api.QueryColumnName;

/**
 * Enum containing the column names for {@link
 * pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper#queryHistoryColumnValues}.
 *
 * @author bv
 */
public enum HistoryQueryColumnName implements QueryColumnName {
  ID("id"),
  BUSINESS_PROCESS_ID("business_process_id"),
  PARENT_BUSINESS_PROCESS_ID("parent_business_process_id"),
  TASK_ID("task_id"),
  EVENT_TYPE("event_type"),
  CREATED("created"),
  USER_ID("user_id"),
  DOMAIN("domain"),
  WORKBASKET_KEY("workbasket_key"),
  POR_COMPANY("por_company"),
  POR_SYSTEM("por_system"),
  POR_INSTANCE("por_instance"),
  POR_TYPE("por_type"),
  POR_VALUE("por_value"),
  TASK_CLASSIFICATION_KEY("task_classification_key"),
  TASK_CLASSIFICATION_CATEGORY("task_classification_category"),
  ATTACHMENT_CLASSIFICATION_KEY("attachment_classification_key"),
  COMMENT("comment"),
  OLD_VALUE("old_value"),
  NEW_VALUE("new_value"),
  CUSTOM_1("custom_1"),
  CUSTOM_2("custom_2"),
  CUSTOM_3("custom_3"),
  CUSTOM_4("custom_4"),
  OLD_DATA("old_data"),
  NEW_DATA("new_data"),
  TYPE("type");

  private String name;

  HistoryQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
