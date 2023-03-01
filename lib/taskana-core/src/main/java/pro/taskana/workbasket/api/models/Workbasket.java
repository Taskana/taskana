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

import java.time.Instant;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/** Workbasket entity interface. */
public interface Workbasket extends WorkbasketSummary {

  /**
   * Sets the name of the Workbasket.
   *
   * @param workbasketName the name of the Workbasket
   */
  void setName(String workbasketName);

  /**
   * Sets the description of the Workbasket.
   *
   * @param description the description of the Workbasket
   */
  void setDescription(String description);

  /**
   * Sets the type of the Workbasket.
   *
   * @param type the type of the Workbasket
   */
  void setType(WorkbasketType type);

  /**
   * Sets the value of the specified {@linkplain WorkbasketCustomField}.
   *
   * @param customField identifies which {@linkplain WorkbasketCustomField} is to be set
   * @param value the value of the {@linkplain WorkbasketCustomField} to be set
   * @deprecated Use {@linkplain #setCustomField(WorkbasketCustomField, String)} instead
   */
  @Deprecated
  void setCustomAttribute(WorkbasketCustomField customField, String value);

  /**
   * Sets the value for the specified {@linkplain WorkbasketCustomField}.
   *
   * @param customField identifies which {@linkplain WorkbasketCustomField} is to be set.
   * @param value the value of the {@linkplain WorkbasketCustomField} to be set
   */
  void setCustomField(WorkbasketCustomField customField, String value);

  /**
   * Sets the value for orgLevel1 attribute.
   *
   * @param orgLevel1 the orgLevel1 property of the Workbasket
   */
  void setOrgLevel1(String orgLevel1);

  /**
   * Sets the value for orgLevel2 attribute.
   *
   * @param orgLevel2 the orgLevel2 property of the Workbasket
   */
  void setOrgLevel2(String orgLevel2);

  /**
   * Sets the value for orgLevel3 attribute.
   *
   * @param orgLevel3 the orgLevel3 property of the Workbasket
   */
  void setOrgLevel3(String orgLevel3);

  /**
   * Sets the value for orgLevel4 attribute.
   *
   * @param orgLevel4 the orgLevel4 of the Workbasket
   */
  void setOrgLevel4(String orgLevel4);

  /**
   * Checks if the Workbasket is marked for deletion.
   *
   * @return the markedForDeletion flag
   */
  boolean isMarkedForDeletion();

  /**
   * Sets the value for markedForDeletion flag.
   *
   * @param markedForDeletion the markedForDeletion flag of the Workbasket
   */
  void setMarkedForDeletion(boolean markedForDeletion);

  /**
   * Duplicates the Workbasket without the id.
   *
   * @param key for the new Workbasket
   * @return a copy of this Workbasket
   */
  Workbasket copy(String key);

  /**
   * Sets the owner of the Workbasket.
   *
   * @param owner the id of the owner of the current Workbasket
   */
  void setOwner(String owner);

  /**
   * Returns the time when the Workbasket was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the Workbasket was modified last time.
   *
   * @return the modified Instant
   */
  Instant getModified();

  /**
   * Returns a summary of the current Workbasket.
   *
   * @return the {@linkplain WorkbasketSummary} object for the current Workbasket
   */
  WorkbasketSummary asSummary();
}
