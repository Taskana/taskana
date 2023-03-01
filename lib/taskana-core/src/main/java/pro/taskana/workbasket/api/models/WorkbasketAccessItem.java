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
package pro.taskana.workbasket.api.models;

import pro.taskana.workbasket.api.WorkbasketPermission;

/**
 * Interface for WorkbasketAccessItem. This interface is used to control access of users to
 * {@linkplain Workbasket Workbaskets}.
 */
public interface WorkbasketAccessItem {

  /**
   * Returns the current id of the WorkbasketAccessItem.
   *
   * @return id
   */
  String getId();

  /**
   * Returns the {@linkplain WorkbasketSummary#getId() id} of the referenced {@linkplain
   * Workbasket}.
   *
   * @return {@linkplain WorkbasketSummary#getId() workbasketId}
   */
  String getWorkbasketId();

  /**
   * Returns the {@linkplain WorkbasketSummary#getKey() key} of the referenced {@linkplain
   * Workbasket}.
   *
   * @return {@linkplain WorkbasketSummary#getKey() key}
   */
  String getWorkbasketKey();

  /**
   * Returns the id of the group or the user for which this WorkbasketAccessItem controls access
   * permissions.
   *
   * @return the accessId, this is the group id or user id
   */
  String getAccessId();

  /**
   * Returns the name of the group or user for which this WorkbasketAccessItem controls access
   * {@linkplain WorkbasketPermission permissions}.
   *
   * @return the accessName, this is the name of the group or user
   */
  String getAccessName();

  /**
   * Set the name of the group or user for which this WorkbasketAccessItem controls access
   * {@linkplain WorkbasketPermission permissions}.
   *
   * @param name the name of the group or user for which this WorkbasketAccessItem controls access
   *     {@linkplain WorkbasketPermission permissions}
   */
  void setAccessName(String name);

  /**
   * Sets a given {@linkplain WorkbasketPermission permissions} for the referenced {@link
   * Workbasket}.
   *
   * @param permission the {@linkplain WorkbasketPermission permissions} which is set
   * @param value the value for the {@linkplain WorkbasketPermission permissions}
   */
  void setPermission(WorkbasketPermission permission, boolean value);

  /**
   * Returns whether the given {@linkplain WorkbasketPermission permissions} is permitted or not.
   *
   * @param permission the {@linkplain WorkbasketPermission permissions} in question
   * @return True, if the given {@linkplain WorkbasketPermission permissions} is permitted;
   *     otherwise false
   */
  boolean getPermission(WorkbasketPermission permission);

  /**
   * Duplicates this WorkbasketAccessItem without the id.
   *
   * @return a copy of this WorkbasketAccessItem
   */
  WorkbasketAccessItem copy();
}
