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
package pro.taskana.spi.history.api.events.workbasket;

public enum WorkbasketHistoryEventType {
  CREATED("CREATED"),
  UPDATED("UPDATED"),
  DELETED("DELETED"),
  MARKED_FOR_DELETION("MARKED_FOR_DELETION"),
  ACCESS_ITEM_CREATED("ACCESS_ITEM_CREATED"),
  ACCESS_ITEM_UPDATED("ACCESS_ITEM_UPDATED"),
  ACCESS_ITEM_DELETED("ACCESS_ITEM_DELETED"),
  ACCESS_ITEMS_UPDATED("ACCESS_ITEMS_UPDATED"),
  ACCESS_ITEM_DELETED_FOR_ACCESS_ID("ACCESS_ITEM_DELETED_FOR_ACCESS_ID"),
  DISTRIBUTION_TARGET_ADDED("DISTRIBUTION_TARGET_ADDED"),
  DISTRIBUTION_TARGET_REMOVED("DISTRIBUTION_TARGET_REMOVED"),
  DISTRIBUTION_TARGETS_UPDATED("DISTRIBUTION_TARGETS_UPDATED");

  private String name;

  WorkbasketHistoryEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
