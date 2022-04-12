package pro.taskana.classification.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Classification entity. */
public class ClassificationImpl extends ClassificationSummaryImpl implements Classification {

  private Boolean isValidInDomain;
  private Instant created;
  private Instant modified;
  private String description;

  public ClassificationImpl() {}

  private ClassificationImpl(ClassificationImpl copyFrom, String key) {
    super(copyFrom);
    isValidInDomain = copyFrom.isValidInDomain;
    created = copyFrom.created;
    modified = copyFrom.modified;
    description = copyFrom.description;
    this.key = key;
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
  public ClassificationImpl copy(String key) {
    return new ClassificationImpl(this, key);
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
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  @Deprecated
  @Override
  public void setCustomAttribute(ClassificationCustomField customField, String value) {
    setCustomField(customField, value);
  }

  @Override
  public void setCustomField(ClassificationCustomField customField, String value) {
    switch (customField) {
      case CUSTOM_1:
        custom1 = value;
        break;
      case CUSTOM_2:
        custom2 = value;
        break;
      case CUSTOM_3:
        custom3 = value;
        break;
      case CUSTOM_4:
        custom4 = value;
        break;
      case CUSTOM_5:
        custom5 = value;
        break;
      case CUSTOM_6:
        custom6 = value;
        break;
      case CUSTOM_7:
        custom7 = value;
        break;
      case CUSTOM_8:
        custom8 = value;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
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
    summary.setApplicationEntryPoint(this.applicationEntryPoint);
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

  @Override
  protected boolean canEqual(Object other) {
    return (other instanceof ClassificationImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isValidInDomain, created, modified, description);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ClassificationImpl)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ClassificationImpl other = (ClassificationImpl) obj;
    return Objects.equals(isValidInDomain, other.isValidInDomain)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(description, other.description);
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
