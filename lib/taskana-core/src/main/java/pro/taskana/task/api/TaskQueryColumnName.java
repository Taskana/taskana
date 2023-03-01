/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
  MANUAL_PRIORITY("t.manual_priority"),
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
  OWNER_LONG_NAME("u.long_name"),
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
  CUSTOM_INT_1("t.custom_int_1"),
  CUSTOM_INT_2("t.custom_int_2"),
  CUSTOM_INT_3("t.custom_int_3"),
  CUSTOM_INT_4("t.custom_int_4"),
  CUSTOM_INT_5("t.custom_int_5"),
  CUSTOM_INT_6("t.custom_int_6"),
  CUSTOM_INT_7("t.custom_int_7"),
  CUSTOM_INT_8("t.custom_int_8"),
  A_CLASSIFICATION_NAME("ac.name"),
  A_CLASSIFICATION_ID("a.classification_id"),
  A_CLASSIFICATION_KEY("a.classification_key"),
  A_CHANNEL("a.channel"),
  A_REF_VALUE("a.ref_value"),
  O_COMPANY("o.company"),
  O_SYSTEM("o.system"),
  O_SYSTEM_INSTANCE("o.system_instance"),
  O_TYPE("o.type"),
  O_VALUE("o.value");

  private final String name;

  TaskQueryColumnName(String name) {
    this.name = name;
  }

  public boolean isAttachmentColumn() {
    return this.name().startsWith("A_");
  }

  public boolean isObjectReferenceColumn() {
    return this.name().startsWith("O_");
  }

  @Override
  public String toString() {
    return name;
  }
}
