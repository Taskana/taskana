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
  KEY("key"),
  TYPE("type"),
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
