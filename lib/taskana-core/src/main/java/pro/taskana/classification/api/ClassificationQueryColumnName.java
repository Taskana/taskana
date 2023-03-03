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
package pro.taskana.classification.api;

import pro.taskana.common.api.QueryColumnName;

/**
 * Enum containing the column names for {@link
 * pro.taskana.classification.internal.ClassificationQueryMapper#queryClassificationColumnValues}.
 */
public enum ClassificationQueryColumnName implements QueryColumnName {
  ID("id"),
  KEY("key"),
  PARENT_ID("parent_id"),
  PARENT_KEY("parent_key"),
  CATEGORY("category"),
  TYPE("type"),
  DOMAIN("domain"),
  VALID_IN_DOMAIN("valid_in_domain"),
  CREATED("created"),
  MODIFIED("modified"),
  NAME("name"),
  DESCRIPTION("description"),
  PRIORITY("priority"),
  SERVICELEVEL("serviceLevel"),
  APPLICATION_ENTRY_POINT("application_entry_point"),
  CUSTOM_1("custom_1"),
  CUSTOM_2("custom_2"),
  CUSTOM_3("custom_3"),
  CUSTOM_4("custom_4"),
  CUSTOM_5("custom_5"),
  CUSTOM_6("custom_6"),
  CUSTOM_7("custom_7"),
  CUSTOM_8("custom_8");

  private final String name;

  ClassificationQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
