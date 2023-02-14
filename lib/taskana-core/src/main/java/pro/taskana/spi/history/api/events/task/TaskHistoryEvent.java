package pro.taskana.spi.history.api.events.task;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.models.TaskSummary;

/** Super class for all task related events. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TaskHistoryEvent {

  protected String id;
  protected String businessProcessId;
  protected String parentBusinessProcessId;
  protected String taskId;
  protected String eventType;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
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

  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }
}
