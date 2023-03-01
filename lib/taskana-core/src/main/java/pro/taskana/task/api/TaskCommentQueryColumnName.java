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

public enum TaskCommentQueryColumnName implements QueryColumnName {
  ID("tc.id"),
  TASK_ID("tc.task_id"),
  TEXT_FIELD("tc.text_field"),
  CREATOR("tc.creator"),
  CREATOR_FULL_NAME("u.full_name"),
  CREATED("tc.created"),
  MODIFIED("tc.modified");

  private final String name;

  TaskCommentQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
