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

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/**
 * Interface for WorkbasketSummary. This is a specific short model-object which only contains the
 * most important information.
 */
public interface WorkbasketSummary {

  /**
   * Returns the id of the {@linkplain Workbasket}.
   *
   * @return id
   */
  String getId();

  /**
   * Returns the key of the {@linkplain Workbasket}.
   *
   * @return key
   */
  String getKey();

  /**
   * Returns the name of the {@linkplain Workbasket}.
   *
   * @return name
   */
  String getName();

  /**
   * Returns the description of the {@linkplain Workbasket}.
   *
   * @return description
   */
  String getDescription();

  /**
   * Returns the owner of the {@linkplain Workbasket}.
   *
   * @return owner
   */
  String getOwner();

  /**
   * Returns the domain of the {@linkplain Workbasket}.
   *
   * @return domain
   */
  String getDomain();

  /**
   * Returns the type of the {@linkplain Workbasket}.
   *
   * @return type
   */
  WorkbasketType getType();

  /**
   * Returns the value of the specified {@linkplain WorkbasketCustomField} of the {@linkplain
   * Workbasket}.
   *
   * @param customField identifies which {@linkplain WorkbasketCustomField} is requested
   * @return the value for the given {@linkplain WorkbasketCustomField}
   * @deprecated Use {@linkplain #getCustomField(WorkbasketCustomField)} instead
   */
  @Deprecated
  String getCustomAttribute(WorkbasketCustomField customField);

  /**
   * Returns the value of the specified {@linkplain WorkbasketCustomField} of the {@linkplain
   * Workbasket}.
   *
   * @param customField identifies which the value of the specified {@linkplain
   *     WorkbasketCustomField} of the {@linkplain Workbasket} is requested
   * @return the value for the given the value of the specified {@linkplain WorkbasketCustomField}
   *     of the {@linkplain Workbasket}
   */
  String getCustomField(WorkbasketCustomField customField);

  /**
   * Returns the orglevel1 of the {@linkplain Workbasket}.
   *
   * @return orglevel1
   */
  String getOrgLevel1();

  /**
   * Returns the orglevel2 of the {@linkplain Workbasket}.
   *
   * @return orglevel2
   */
  String getOrgLevel2();

  /**
   * Returns the orglevel3 of the {@linkplain Workbasket}.
   *
   * @return orglevel3
   */
  String getOrgLevel3();

  /**
   * Returns the orglevel4 of the {@linkplain Workbasket}.
   *
   * @return orglevel4
   */
  String getOrgLevel4();

  /**
   * Checks if the {@linkplain Workbasket} is marked for deletion.
   *
   * @return the markedForDeletion flag
   */
  boolean isMarkedForDeletion();

  /**
   * Duplicates this WorkbasketSummary without the id.
   *
   * @return a copy of this WorkbasketSummary
   */
  WorkbasketSummary copy();
}
