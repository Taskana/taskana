/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.workbasket.rest.models;

import org.springframework.hateoas.RepresentationModel;

import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** EntityModel class for {@link WorkbasketSummary}. */
public class WorkbasketSummaryRepresentationModel
    extends RepresentationModel<WorkbasketSummaryRepresentationModel> {

  /** Unique Id. */
  protected String workbasketId;
  /** the professional key for the workbasket. */
  protected String key;
  /** The name of the workbasket. */
  protected String name;
  /** The domain the workbasket belongs to. */
  protected String domain;
  /** The type of the workbasket. */
  protected WorkbasketType type;
  /** the description of the workbasket. */
  protected String description;
  /**
   * The owner of the workbasket. The owner is responsible for the on-time completion of all tasks
   * in the workbasket.
   */
  protected String owner;
  /** A custom property with name "1". */
  protected String custom1;
  /** A custom property with name "2". */
  protected String custom2;
  /** A custom property with name "3". */
  protected String custom3;
  /** A custom property with name "4". */
  protected String custom4;
  /**
   * The first Org Level (the top one).
   *
   * <p>The Org Level is an association with an org hierarchy level in the organization. The values
   * are used for monitoring and statistical purposes and should reflect who is responsible of the
   * tasks in the workbasket.
   */
  protected String orgLevel1;
  /** The second Org Level. */
  protected String orgLevel2;
  /** The third Org Level. */
  protected String orgLevel3;
  /** The fourth Org Level (the lowest one). */
  protected String orgLevel4;
  /** Identifier to tell if this workbasket can be deleted. */
  private boolean markedForDeletion;

  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public WorkbasketType getType() {
    return type;
  }

  public void setType(WorkbasketType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  public String getOrgLevel1() {
    return orgLevel1;
  }

  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  public String getOrgLevel2() {
    return orgLevel2;
  }

  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  public String getOrgLevel3() {
    return orgLevel3;
  }

  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  public String getOrgLevel4() {
    return orgLevel4;
  }

  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  public boolean getMarkedForDeletion() {
    return markedForDeletion;
  }

  public void setMarkedForDeletion(boolean markedForDeletion) {
    this.markedForDeletion = markedForDeletion;
  }
}
