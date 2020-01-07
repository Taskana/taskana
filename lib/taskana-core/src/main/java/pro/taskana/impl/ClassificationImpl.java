package pro.taskana.impl;

import java.time.Instant;
import java.util.Objects;

import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;

/** Classification entity. */
public class ClassificationImpl extends ClassificationSummaryImpl implements Classification {

  private Boolean isValidInDomain;
  private Instant created;
  private Instant modified;
  private String description;
  private String applicationEntryPoint;

  ClassificationImpl() {}

  ClassificationImpl(ClassificationImpl classification) {
    this.id = classification.getId();
    this.key = classification.getKey();
    this.parentId = classification.getParentId();
    this.parentKey = classification.getParentKey();
    this.category = classification.getCategory();
    this.type = classification.getType();
    this.domain = classification.getDomain();
    this.isValidInDomain = classification.getIsValidInDomain();
    this.created = classification.getCreated();
    this.modified = classification.getModified();
    this.name = classification.getName();
    this.description = classification.getDescription();
    this.priority = classification.getPriority();
    this.serviceLevel = classification.getServiceLevel();
    this.applicationEntryPoint = classification.getApplicationEntryPoint();
    this.custom1 = classification.getCustom1();
    this.custom2 = classification.getCustom2();
    this.custom3 = classification.getCustom3();
    this.custom4 = classification.getCustom4();
    this.custom5 = classification.getCustom5();
    this.custom6 = classification.getCustom6();
    this.custom7 = classification.getCustom7();
    this.custom8 = classification.getCustom8();
  }

  @Override
  public Boolean getIsValidInDomain() {
    return isValidInDomain;
  }

  @Override
  public void setIsValidInDomain(Boolean isValidInDomain) {
    this.isValidInDomain = isValidInDomain;
  }

  @Override
  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  @Override
  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getApplicationEntryPoint() {
    return applicationEntryPoint;
  }

  @Override
  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint = applicationEntryPoint;
  }

  @Override
  public ClassificationSummary asSummary() {
    ClassificationSummaryImpl summary = new ClassificationSummaryImpl();
    summary.setCategory(this.category);
    summary.setDomain(this.domain);
    summary.setId(this.id);
    summary.setKey(this.key);
    summary.setName(this.name);
    summary.setType(this.type);
    summary.setParentId(this.parentId);
    summary.setParentKey(this.parentKey);
    summary.setPriority(this.priority);
    summary.setServiceLevel(this.serviceLevel);
    summary.setCustom1(custom1);
    summary.setCustom2(custom2);
    summary.setCustom3(custom3);
    summary.setCustom4(custom4);
    summary.setCustom5(custom5);
    summary.setCustom6(custom6);
    summary.setCustom7(custom7);
    summary.setCustom8(custom8);
    return summary;
  }

  protected boolean canEqual(Object other) {
    return (other instanceof ClassificationImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), isValidInDomain, created, modified, description, applicationEntryPoint);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ClassificationImpl other = (ClassificationImpl) obj;
    return Objects.equals(isValidInDomain, other.isValidInDomain)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(description, other.description)
        && Objects.equals(applicationEntryPoint, other.applicationEntryPoint);
  }

  @Override
  public String toString() {
    return "ClassificationImpl [id="
        + id
        + ", key="
        + key
        + ", parentId="
        + parentId
        + ", parentKey="
        + parentKey
        + ", category="
        + category
        + ", type="
        + type
        + ", domain="
        + domain
        + ", isValidInDomain="
        + isValidInDomain
        + ", created="
        + created
        + ", modified="
        + modified
        + ", name="
        + name
        + ", description="
        + description
        + ", priority="
        + priority
        + ", serviceLevel="
        + serviceLevel
        + ", applicationEntryPoint="
        + applicationEntryPoint
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
