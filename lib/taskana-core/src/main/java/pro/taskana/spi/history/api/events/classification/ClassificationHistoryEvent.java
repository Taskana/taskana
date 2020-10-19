package pro.taskana.spi.history.api.events.classification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.SystemException;

/** Super class for all classification related events. */
public class ClassificationHistoryEvent {

  protected String id;
  protected String eventType;
  protected Instant created;
  protected String userId;
  protected String classificationId;
  protected String applicationEntryPoint;
  protected String category;
  protected String domain;
  protected String key;
  protected String name;
  protected String parentId;
  protected String parentKey;
  protected int priority;
  protected String serviceLevel;
  protected String type;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String custom5;
  protected String custom6;
  protected String custom7;
  protected String custom8;
  protected String details;

  public ClassificationHistoryEvent() {}

  public ClassificationHistoryEvent(
      String id, ClassificationSummary classification, String userId, String details) {
    this.id = id;
    this.userId = userId;
    classificationId = classification.getId();
    applicationEntryPoint = classification.getApplicationEntryPoint();
    category = classification.getCategory();
    domain = classification.getDomain();
    key = classification.getKey();
    name = classification.getName();
    parentId = classification.getParentId();
    parentKey = classification.getParentKey();
    priority = classification.getPriority();
    serviceLevel = classification.getServiceLevel();
    type = classification.getType();
    custom1 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_1);
    custom2 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_2);
    custom3 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_3);
    custom4 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_4);
    custom5 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_5);
    custom6 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_6);
    custom7 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_7);
    custom8 = classification.getCustomAttribute(ClassificationCustomField.CUSTOM_8);
    this.details = details;
  }

  public void setCustomAttribute(ClassificationCustomField customField, String value) {
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

  public String getCustomAttribute(ClassificationCustomField customField) {
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getClassificationId() {
    return classificationId;
  }

  public void setClassificationId(String classificationId) {
    this.classificationId = classificationId;
  }

  public String getApplicationEntryPoint() {
    return applicationEntryPoint;
  }

  public void setApplicationEntryPoint(String applicationEntryPoint) {
    this.applicationEntryPoint = applicationEntryPoint;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
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

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getParentKey() {
    return parentKey;
  }

  public void setParentKey(String parentKey) {
    this.parentKey = parentKey;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getId(),
        getEventType(),
        getCreated(),
        getUserId(),
        getClassificationId(),
        getApplicationEntryPoint(),
        getCategory(),
        getDomain(),
        getKey(),
        getName(),
        getParentId(),
        getParentKey(),
        getPriority(),
        getServiceLevel(),
        getType(),
        custom1,
        custom2,
        custom3,
        custom4,
        custom5,
        custom6,
        custom7,
        custom8,
        getDetails());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ClassificationHistoryEvent)) {
      return false;
    }
    ClassificationHistoryEvent other = (ClassificationHistoryEvent) obj;
    return getPriority() == other.getPriority()
        && Objects.equals(getId(), other.getId())
        && Objects.equals(getEventType(), other.getEventType())
        && Objects.equals(getCreated(), other.getCreated())
        && Objects.equals(getUserId(), other.getUserId())
        && Objects.equals(getClassificationId(), other.getClassificationId())
        && Objects.equals(getApplicationEntryPoint(), other.getApplicationEntryPoint())
        && Objects.equals(getCategory(), other.getCategory())
        && Objects.equals(getDomain(), other.getDomain())
        && Objects.equals(getKey(), other.getKey())
        && Objects.equals(getName(), other.getName())
        && Objects.equals(getParentId(), other.getParentId())
        && Objects.equals(getParentKey(), other.getParentKey())
        && Objects.equals(getServiceLevel(), other.getServiceLevel())
        && Objects.equals(getType(), other.getType())
        && Objects.equals(custom1, other.custom1)
        && Objects.equals(custom2, other.custom2)
        && Objects.equals(custom3, other.custom3)
        && Objects.equals(custom4, other.custom4)
        && Objects.equals(custom5, other.custom5)
        && Objects.equals(custom6, other.custom6)
        && Objects.equals(custom7, other.custom7)
        && Objects.equals(custom8, other.custom8)
        && Objects.equals(getDetails(), other.getDetails());
  }

  @Override
  public String toString() {
    return "ClassificationHistoryEvent [id="
        + id
        + ", eventType="
        + eventType
        + ", created="
        + created
        + ", userId="
        + userId
        + ", classificationId="
        + classificationId
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
        + ", details="
        + details
        + "]";
  }
}
