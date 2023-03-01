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
package pro.taskana.spi.history.api.events.task;

public enum TaskHistoryEventType {
  CREATED("CREATED"),
  UPDATED("UPDATED"),
  CLAIMED("CLAIMED"),
  CLAIM_CANCELLED("CLAIM_CANCELLED"),
  REQUESTED_REVIEW("REQUESTED_REVIEW"),
  CHANGES_REQUESTED("CHANGES_REQUESTED"),
  COMPLETED("COMPLETED"),
  CANCELLED("CANCELLED"),
  TERMINATED("TERMINATED"),
  TRANSFERRED("TRANSFERRED");

  private String name;

  TaskHistoryEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
