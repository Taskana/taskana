package pro.taskana.task.api;

import pro.taskana.common.api.QueryColumnName;

/** Enum containing the column names for TaskQueryMapper.queryTaskColumnValues. */
public enum TaskQueryColumnName implements QueryColumnName {
  ID("t.id"),
  EXTERNAL_ID("t.external_id"),
  CREATED("t.created"),
  CLAIMED("t.claimed"),
  COMPLETED("t.completed"),
  MODIFIED("t.modified"),
  PLANNED("t.planned"),
  RECEIVED("t.received"),
  DUE("t.due"),
  NAME("t.name"),
  CREATOR("t.creator"),
  DESCRIPTION("t.description"),
  NOTE("t.note"),
  PRIORITY("t.priority"),
  STATE("t.state"),
  CLASSIFICATION_CATEGORY("t.classification_category"),
  CLASSIFICATION_KEY("t.classification_key"),
  CLASSIFICATION_ID("t.classification_id"),
  CLASSIFICATION_NAME("c.name"),
  WORKBASKET_ID("t.workbasket_id"),
  WORKBASKET_KEY("t.workbasket_key"),
  DOMAIN("t.domain"),
  BUSINESS_PROCESS_ID("t.business_process_id"),
  PARENT_BUSINESS_PROCESS_ID("t.parent_business_process_id"),
  OWNER("t.owner"),
  POR_COMPANY("t.por_company"),
  POR_SYSTEM("t.por_system"),
  POR_INSTANCE("t.por_instance"),
  POR_TYPE("t.por_type"),
  POR_VALUE("t.por_value"),
  IS_READ("t.is_read"),
  IS_TRANSFERRED("t.is_transferred"),
  CUSTOM_1("t.custom_1"),
  CUSTOM_2("t.custom_2"),
  CUSTOM_3("t.custom_3"),
  CUSTOM_4("t.custom_4"),
  CUSTOM_5("t.custom_5"),
  CUSTOM_6("t.custom_6"),
  CUSTOM_7("t.custom_7"),
  CUSTOM_8("t.custom_8"),
  CUSTOM_9("t.custom_9"),
  CUSTOM_10("t.custom_10"),
  CUSTOM_11("t.custom_11"),
  CUSTOM_12("t.custom_12"),
  CUSTOM_13("t.custom_13"),
  CUSTOM_14("t.custom_14"),
  CUSTOM_15("t.custom_15"),
  CUSTOM_16("t.custom_16"),
  A_CLASSIFICATION_NAME("ac.name"),
  A_CLASSIFICATION_ID("a.classification_id"),
  A_CLASSIFICATION_KEY("a.classification_key"),
  A_CHANNEL("a.channel"),
  A_REF_VALUE("a.ref_value");

  private final String name;

  TaskQueryColumnName(String name) {
    this.name = name;
  }

  public boolean isAttachmentColumn() {
    return this.name().startsWith("A_");
  }

  @Override
  public String toString() {
    return name;
  }
}
