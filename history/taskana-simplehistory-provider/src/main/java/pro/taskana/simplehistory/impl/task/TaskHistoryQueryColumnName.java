/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package pro.taskana.simplehistory.impl.task;

import pro.taskana.common.api.QueryColumnName;

/** Enum containing the column names for {@link TaskHistoryQueryMapper#queryHistoryColumnValues}. */
public enum TaskHistoryQueryColumnName implements QueryColumnName {
  ID("id"),
  BUSINESS_PROCESS_ID("business_process_id"),
  PARENT_BUSINESS_PROCESS_ID("parent_business_process_id"),
  TASK_ID("task_id"),
  EVENT_TYPE("event_type"),
  CREATED("created"),
  USER_ID("t.user_id"),
  USER_LONG_NAME("u.long_name"),
  DOMAIN("domain"),
  WORKBASKET_KEY("workbasket_key"),
  POR_COMPANY("por_company"),
  POR_SYSTEM("por_system"),
  POR_INSTANCE("por_instance"),
  POR_TYPE("por_type"),
  POR_VALUE("por_value"),
  TASK_OWNER_LONG_NAME("o.long_name"),
  TASK_CLASSIFICATION_KEY("task_classification_key"),
  TASK_CLASSIFICATION_CATEGORY("task_classification_category"),
  ATTACHMENT_CLASSIFICATION_KEY("attachment_classification_key"),
  OLD_VALUE("old_value"),
  NEW_VALUE("new_value"),
  CUSTOM_1("custom_1"),
  CUSTOM_2("custom_2"),
  CUSTOM_3("custom_3"),
  CUSTOM_4("custom_4");

  private String name;

  TaskHistoryQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
