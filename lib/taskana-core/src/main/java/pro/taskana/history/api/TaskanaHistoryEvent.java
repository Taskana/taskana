package pro.taskana.history.api;

import java.time.Instant;

import pro.taskana.common.internal.security.CurrentUserContext;

/** Super class for all specific events from the TASKANA engine. */
public class TaskanaHistoryEvent {

  protected long id;
  protected String businessProcessId;
  protected String parentBusinessProcessId;
  protected String taskId;
  protected String eventType;
  protected Instant created;
  protected String userId;
  protected String domain;
  protected String workbasketKey;
  protected String porCompany;
  protected String porSystem;
  protected String porInstance;
  protected String porType;
  protected String porValue;
  protected String taskClassificationKey;
  protected String taskClassificationCategory;
  protected String attachmentClassificationKey;
  protected String comment;
  protected String oldValue;
  protected String newValue;
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String oldData;
  protected String newData;

  public TaskanaHistoryEvent() {
    userId = CurrentUserContext.getUserid();
  }

  public long getId() {
    return id;
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

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
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

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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

  public String getOldData() {
    return oldData;
  }

  public void setOldData(String oldData) {
    this.oldData = oldData;
  }

  public String getNewData() {
    return newData;
  }

  public void setNewData(String newData) {
    this.newData = newData;
  }

  @Override
  public String toString() {
    return "TaskanaHistoryEvent ["
        + "id="
        + this.id
        + ", businessProcessId="
        + this.businessProcessId
        + ", parentBusinessProcessId="
        + this.parentBusinessProcessId
        + ", taskId="
        + this.taskId
        + ", eventType="
        + this.eventType
        + ", created="
        + this.created
        + ", userId="
        + this.userId
        + ", domain="
        + this.domain
        + ", workbasketKey="
        + this.workbasketKey
        + ", taskClassificationKey="
        + this.taskClassificationKey
        + ", attachmentClassificationKey="
        + this.attachmentClassificationKey
        + ", oldValue="
        + this.oldValue
        + ", newValue="
        + this.newValue
        + ", oldData="
        + this.oldData
        + ", newData="
        + this.newData
        + "]";
  }
}
