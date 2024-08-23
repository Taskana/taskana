package io.kadai.simplehistory.rest.models;

import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;

/** Resource class for {@link TaskHistoryEvent}. */
public class TaskHistoryEventRepresentationModel
    extends RepresentationModel<TaskHistoryEventRepresentationModel> {

  /** Unique Id. */
  @Schema(name = "taskHistoryId", description = "Unique Id.")
  private String taskHistoryId;

  /** The Id of the business process. */
  @Schema(name = "businessProcessId", description = "The Id of the business process.")
  private String businessProcessId;

  /** The Id of the parent business process. */
  @Schema(name = "parentBusinessProcessId", description = "The Id of the parent business process.")
  private String parentBusinessProcessId;

  /** The Id of the task. */
  @Schema(name = "taskId", description = "The Id of the task.")
  private String taskId;

  /** The type of the event. */
  @Schema(name = "eventType", description = "The type of the event.")
  private String eventType;

  /**
   * The time of event creation.
   *
   * <p>The format is ISO-8601.
   */
  @Schema(name = "created", description = "The time of event creation.<p>The format is ISO-8601.")
  private Instant created;

  /** The Id of the user. */
  @Schema(name = "userId", description = "The Id of the user.")
  private String userId;

  /** The long name of the user. */
  @Schema(name = "userLongName", description = "The long name of the user.")
  private String userLongName;

  /** Domain. */
  @Schema(name = "domain", description = "Domain.")
  private String domain;

  /** The key of the Workbasket. */
  @Schema(name = "workbasketKey", description = "The key of the Workbasket.")
  private String workbasketKey;

  /** The company the referenced primary object belongs to. */
  @Schema(
      name = "porCompany",
      description = "The company the referenced primary object belongs to.")
  private String porCompany;

  /** The type of the referenced primary object (contract, claim, policy, customer, ...). */
  @Schema(
      name = "porType",
      description =
          "The type of the referenced primary object (contract, claim, policy, customer, ...).")
  private String porType;

  /** The (kind of) system, the referenced primary object resides in (e.g. SAP, MySystem A, ...). */
  @Schema(
      name = "porSystem",
      description =
          "The (kind of) system, the referenced primary object resides in (e.g. SAP, MySystem A, "
              + "...).")
  private String porSystem;

  /** The instance of the system where the referenced primary object is located. */
  @Schema(
      name = "porInstance",
      description = "The instance of the system where the referenced primary object is located.")
  private String porInstance;

  /** The value of the primary object reference. */
  @Schema(name = "porValue", description = "The value of the primary object reference.")
  private String porValue;

  /** The long name of the task owner. */
  @Schema(name = "taskOwnerLongName", description = "The long name of the task owner.")
  private String taskOwnerLongName;

  /** The key of the task's classification. */
  @Schema(name = "taskClassificationKey", description = "The key of the task's classification.")
  private String taskClassificationKey;

  /** The category of the task's classification. */
  @Schema(
      name = "taskClassificationCategory",
      description = "The category of the task's classification.")
  private String taskClassificationCategory;

  /** The classification key of the task's attachment. */
  @Schema(
      name = "attachmentClassificationKey",
      description = "The classification key of the task's attachment.")
  private String attachmentClassificationKey;

  /** The old value. */
  @Schema(name = "oldValue", description = "The old value.")
  private String oldValue;

  /** The new value. */
  @Schema(name = "newValue", description = "The new value.")
  private String newValue;

  /** A custom property with name "1". */
  @Schema(name = "custom1", description = "A custom property with name '1'.")
  private String custom1;

  /** A custom property with name "2". */
  @Schema(name = "custom2", description = "A custom property with name '2'.")
  private String custom2;

  /** A custom property with name "3". */
  @Schema(name = "custom3", description = "A custom property with name '3'.")
  private String custom3;

  /** A custom property with name "4". */
  @Schema(name = "custom4", description = "A custom property with name '4'.")
  private String custom4;

  /** details of changes within the task. */
  @Schema(name = "details", description = "details of changes within the task.")
  private String details;

  public String getTaskHistoryId() {
    return taskHistoryId;
  }

  public void setTaskHistoryId(String taskHistoryId) {
    this.taskHistoryId = taskHistoryId;
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

  public String getUserLongName() {
    return userLongName;
  }

  public void setUserLongName(String userLongName) {
    this.userLongName = userLongName;
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

  public String getPorType() {
    return porType;
  }

  public void setPorType(String porType) {
    this.porType = porType;
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

  public String getTaskOwnerLongName() {
    return taskOwnerLongName;
  }

  public void setTaskOwnerLongName(String taskOwnerLongName) {
    this.taskOwnerLongName = taskOwnerLongName;
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

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  @Override
  public String toString() {
    return "TaskHistoryEventRepresentationModel [taskHistoryId="
        + taskHistoryId
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
        + ", domain="
        + domain
        + ", workbasketKey="
        + workbasketKey
        + ", porCompany="
        + porCompany
        + ", porType="
        + porType
        + ", porSystem="
        + porSystem
        + ", porInstance="
        + porInstance
        + ", porValue="
        + porValue
        + ", taskOwnerLongName="
        + taskOwnerLongName
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
