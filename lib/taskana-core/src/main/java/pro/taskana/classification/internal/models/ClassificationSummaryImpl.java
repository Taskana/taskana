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
package pro.taskana.classification.internal.models;

import java.util.Objects;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Implementation for the short summaries of a classification entity. */
public class ClassificationSummaryImpl implements ClassificationSummary {

  protected String id;
  protected String applicationEntryPoint;
  protected String category;
  protected String domain;
  protected String key;
  protected String name;
  protected String parentId;
  protected String parentKey;
  protected int priority;
  protected String serviceLevel; // PddDThhHmmM
  protected String type;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String custom5;
  protected String custom6;
  protected String custom7;
  protected String custom8;

  public ClassificationSummaryImpl() {}

  protected ClassificationSummaryImpl(ClassificationSummaryImpl copyFrom) {
    applicationEntryPoint = copyFrom.applicationEntryPoint;
    category = copyFrom.category;
    domain = copyFrom.domain;
    name = copyFrom.name;
    parentId = copyFrom.parentId;
    parentKey = copyFrom.parentKey;
    priority = copyFrom.priority;
    serviceLevel = copyFrom.serviceLevel;
    type = copyFrom.type;
    custom1 = copyFrom.custom1;
    custom2 = copyFrom.custom2;
    custom3 = copyFrom.custom3;
    custom4 = copyFrom.custom4;
    custom5 = copyFrom.custom5;
    custom6 = copyFrom.custom6;
    custom7 = copyFrom.custom7;
    custom8 = copyFrom.custom8;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key == null ? null : key.trim();
  }

  @Override
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain == null ? null : domain.trim();
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  @Override
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @Override
  public String getParentKey() {
    return parentKey;
  }

  public void setParentKey(String parentKey) {
    this.parentKey = parentKey;
  }

  @Override
  public String getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  @Override
  public String getApplicationEntryPoint() {
    return this.applicationEntryPoint;
  }

  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint =
        applicationEntryPoint == null ? null : applicationEntryPoint.trim();
  }

  @Override
  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Deprecated
  @Override
  public String getCustomAttribute(ClassificationCustomField customField) {
    return getCustomField(customField);
  }

  @Override
  public String getCustomField(ClassificationCustomField customField) {
    switch (customField) {
      case CUSTOM_1:
        return custom1;
      case CUSTOM_2:
        return custom2;
      case CUSTOM_3:
        return custom3;
      case CUSTOM_4:
        return custom4;
      case CUSTOM_5:
        return custom5;
      case CUSTOM_6:
        return custom6;
      case CUSTOM_7:
        return custom7;
      case CUSTOM_8:
        return custom8;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  @Override
  public ClassificationSummaryImpl copy() {
    return new ClassificationSummaryImpl(this);
  }

  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1 == null ? null : custom1.trim();
  }

  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2 == null ? null : custom2.trim();
  }

  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3 == null ? null : custom3.trim();
  }

  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4 == null ? null : custom4.trim();
  }

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5 == null ? null : custom5.trim();
  }

  public String getCustom6() {
    return custom6;
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6 == null ? null : custom6.trim();
  }

  public String getCustom7() {
    return custom7;
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7 == null ? null : custom7.trim();
  }

  public String getCustom8() {
    return custom8;
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8 == null ? null : custom8.trim();
  }

  protected boolean canEqual(Object other) {
    return (other instanceof ClassificationSummaryImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        applicationEntryPoint,
        key,
        category,
        type,
        domain,
        name,
        parentId,
        parentKey,
        priority,
        serviceLevel,
        custom1,
        custom2,
        custom3,
        custom4,
        custom5,
        custom6,
        custom7,
        custom8);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ClassificationSummaryImpl)) {
      return false;
    }
    ClassificationSummaryImpl other = (ClassificationSummaryImpl) obj;

    if (!other.canEqual(this)) {
      return false;
    }
    return priority == other.priority
        && Objects.equals(id, other.id)
        && Objects.equals(applicationEntryPoint, other.applicationEntryPoint)
        && Objects.equals(key, other.key)
        && Objects.equals(category, other.category)
        && Objects.equals(type, other.type)
        && Objects.equals(domain, other.domain)
        && Objects.equals(name, other.name)
        && Objects.equals(parentId, other.parentId)
        && Objects.equals(parentKey, other.parentKey)
        && Objects.equals(serviceLevel, other.serviceLevel)
        && Objects.equals(custom1, other.custom1)
        && Objects.equals(custom2, other.custom2)
        && Objects.equals(custom3, other.custom3)
        && Objects.equals(custom4, other.custom4)
        && Objects.equals(custom5, other.custom5)
        && Objects.equals(custom6, other.custom6)
        && Objects.equals(custom7, other.custom7)
        && Objects.equals(custom8, other.custom8);
  }

  @Override
  public String toString() {
    return "ClassificationSummaryImpl [id="
        + id
        + ", applicationEntryPoint="
        + applicationEntryPoint
        + ", category="
        + category
        + ", domain="
        + domain
        + ", key="
        + key
        + ", name="
        + name
        + ", parentId="
        + parentId
        + ", parentKey="
        + parentKey
        + ", priority="
        + priority
        + ", serviceLevel="
        + serviceLevel
        + ", type="
        + type
        + ", custom1="
        + custom1
        + ", custom2="
        + custom2
        + ", custom3="
        + custom3
        + ", custom4="
        + custom4
        + ", custom5="
        + custom5
        + ", custom6="
        + custom6
        + ", custom7="
        + custom7
        + ", custom8="
        + custom8
        + "]";
  }
}
