package io.kadai.spi.history.api.events.task;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.task.api.models.TaskSummary;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Super class for all task related events. */
public class TaskHistoryEvent {

  protected String id;
  protected String businessProcessId;
  protected String parentBusinessProcessId;
  protected String taskId;
  protected String eventType;
  protected Instant created;
  protected String userId;
  protected String userLongName;
  protected String domain;
  protected String workbasketKey;
  protected String porCompany;
  protected String porSystem;
  protected String porInstance;
  protected String porType;
  protected String porValue;
  protected String taskOwnerLongName;
  protected String taskClassificationKey;
  protected String taskClassificationCategory;
  protected String attachmentClassificationKey;
  protected String oldValue;
  protected String newValue;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String details;

  public TaskHistoryEvent() {}

  public TaskHistoryEvent(String id, TaskSummary task, String userId, String details) {
    this.id = id;
    this.userId = userId;
    this.details = details;
    taskId = task.getId();
    businessProcessId = task.getBusinessProcessId();
    parentBusinessProcessId = task.getParentBusinessProcessId();
    domain = task.getDomain();
    workbasketKey = task.getWorkbasketSummary().getKey();
    if (task.getClassificationSummary() != null) {
      taskClassificationCategory = task.getClassificationSummary().getCategory();
      taskClassificationKey = task.getClassificationSummary().getKey();
    }
    if (!task.getAttachmentSummaries().isEmpty()) {
      attachmentClassificationKey =
          task.getAttachmentSummaries().get(0).getClassificationSummary().getKey();
    }
    if (task.getPrimaryObjRef() != null) {
      porCompany = task.getPrimaryObjRef().getCompany();
      porSystem = task.getPrimaryObjRef().getSystem();
      porInstance = task.getPrimaryObjRef().getSystemInstance();
      porType = task.getPrimaryObjRef().getType();
      porValue = task.getPrimaryObjRef().getValue();
    }
  }

  public void setCustomAttribute(TaskHistoryCustomField customField, String value) {
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
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  public String getCustomAttribute(TaskHistoryCustomField customField) {
    switch (customField) {
      case CUSTOM_1:
        return custom1;
      case CUSTOM_2:
        return custom2;
      case CUSTOM_3:
        return custom3;
      case CUSTOM_4:
        return custom4;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  public String getBusinessProcessId() {
    return businessProcessId;
  }

  public void setBusinessProcessId(String businessProcessId) {
    this.businessProcessId = businessProcessId;
  }

  public String getParentBusinessProcessId() {
    return parentBusinessProcessId;
  }

  public void setParentBusinessProcessId(String parentBusinessProcessId) {
    this.parentBusinessProcessId = parentBusinessProcessId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  public String getPorCompany() {
    return porCompany;
  }

  public void setPorCompany(String porCompany) {
    this.porCompany = porCompany;
  }

  public String getPorSystem() {
    return porSystem;
  }

  public void setPorSystem(String porSystem) {
    this.porSystem = porSystem;
  }

  public String getPorInstance() {
    return porInstance;
  }

  public void setPorInstance(String porInstance) {
    this.porInstance = porInstance;
  }

  public String getPorType() {
    return porType;
  }

  public void setPorType(String porType) {
    this.porType = porType;
  }

  public String getPorValue() {
    return porValue;
  }

  public void setPorValue(String porValue) {
    this.porValue = porValue;
  }

  public String getTaskOwnerLongName() {
    return taskOwnerLongName;
  }

  public void setTaskOwnerLongName(String taskOwnerLongName) {
    this.taskOwnerLongName = taskOwnerLongName;
  }

  public String getTaskClassificationKey() {
    return taskClassificationKey;
  }

  public void setTaskClassificationKey(String taskClassificationKey) {
    this.taskClassificationKey = taskClassificationKey;
  }

  public String getTaskClassificationCategory() {
    return taskClassificationCategory;
  }

  public void setTaskClassificationCategory(String taskClassificationCategory) {
    this.taskClassificationCategory = taskClassificationCategory;
  }

  public String getAttachmentClassificationKey() {
    return attachmentClassificationKey;
  }

  public void setAttachmentClassificationKey(String attachmentClassificationKey) {
    this.attachmentClassificationKey = attachmentClassificationKey;
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

  public String getUserLongName() {
    return userLongName;
  }

  public void setUserLongName(String userLongName) {
    this.userLongName = userLongName;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
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
        getBusinessProcessId(),
        getParentBusinessProcessId(),
        getTaskId(),
        getEventType(),
        getCreated(),
        getUserId(),
        getDomain(),
        getWorkbasketKey(),
        getPorCompany(),
        getPorSystem(),
        getPorInstance(),
        getPorType(),
        getPorValue(),
        getTaskClassificationKey(),
        getTaskClassificationCategory(),
        getAttachmentClassificationKey(),
        getOldValue(),
        getNewValue(),
        custom1,
        custom2,
        custom3,
        custom4,
        getDetails());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TaskHistoryEvent)) {
      return false;
    }
    TaskHistoryEvent other = (TaskHistoryEvent) obj;
    return Objects.equals(getId(), other.getId())
        && Objects.equals(getBusinessProcessId(), other.getBusinessProcessId())
        && Objects.equals(getParentBusinessProcessId(), other.getParentBusinessProcessId())
        && Objects.equals(getTaskId(), other.getTaskId())
        && Objects.equals(getEventType(), other.getEventType())
        && Objects.equals(getCreated(), other.getCreated())
        && Objects.equals(getUserId(), other.getUserId())
        && Objects.equals(getDomain(), other.getDomain())
        && Objects.equals(getWorkbasketKey(), other.getWorkbasketKey())
        && Objects.equals(getPorCompany(), other.getPorCompany())
        && Objects.equals(getPorSystem(), other.getPorSystem())
        && Objects.equals(getPorInstance(), other.getPorInstance())
        && Objects.equals(getPorType(), other.getPorType())
        && Objects.equals(getPorValue(), other.getPorValue())
        && Objects.equals(getTaskClassificationKey(), other.getTaskClassificationKey())
        && Objects.equals(getTaskClassificationCategory(), other.getTaskClassificationCategory())
        && Objects.equals(getAttachmentClassificationKey(), other.getAttachmentClassificationKey())
        && Objects.equals(getOldValue(), other.getOldValue())
        && Objects.equals(getNewValue(), other.getNewValue())
        && Objects.equals(custom1, other.custom1)
        && Objects.equals(custom2, other.custom2)
        && Objects.equals(custom3, other.custom3)
        && Objects.equals(custom4, other.custom4)
        && Objects.equals(getDetails(), other.getDetails());
  }

  @Override
  public String toString() {
    return "TaskHistoryEvent [id="
        + id
        + ", businessProcessId="
        + businessProcessId
        + ", parentBusinessProcessId="
        + parentBusinessProcessId
        + ", taskId="
        + taskId
        + ", eventType="
        + eventType
        + ", created="
        + created
        + ", userId="
        + userId
        + ", userLongName="
        + userLongName
        + ", taskOwnerLongName="
        + taskOwnerLongName
        + ", domain="
        + domain
        + ", workbasketKey="
        + workbasketKey
        + ", porCompany="
        + porCompany
        + ", porSystem="
        + porSystem
        + ", porInstance="
        + porInstance
        + ", porType="
        + porType
        + ", porValue="
        + porValue
        + ", taskClassificationKey="
        + taskClassificationKey
        + ", taskClassificationCategory="
        + taskClassificationCategory
        + ", attachmentClassificationKey="
        + attachmentClassificationKey
        + ", oldValue="
        + oldValue
        + ", newValue="
        + newValue
        + ", custom1="
        + custom1
        + ", custom2="
        + custom2
        + ", custom3="
        + custom3
        + ", custom4="
        + custom4
        + ", details="
        + details
        + "]";
  }
}
