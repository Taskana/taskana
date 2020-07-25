package pro.taskana.spi.history.api.events.task;

import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Super class for all task related events. */
public class TaskEvent extends TaskanaHistoryEvent {

  public TaskEvent(String id, Task task, String userId, String details) {
    super(id, userId, details);
    taskId = task.getId();
    businessProcessId = task.getBusinessProcessId();
    parentBusinessProcessId = task.getParentBusinessProcessId();
    domain = task.getDomain();
    workbasketKey = task.getWorkbasketKey();
    taskClassificationCategory = task.getClassificationCategory();
    if (task.getClassificationSummary() != null) {
      taskClassificationKey = task.getClassificationSummary().getKey();
    }
    if (!task.getAttachments().isEmpty()) {
      attachmentClassificationKey =
          task.getAttachments().get(0).getClassificationSummary().getKey();
    }
    if (task.getPrimaryObjRef() != null) {
      porCompany = task.getPrimaryObjRef().getCompany();
      porSystem = task.getPrimaryObjRef().getSystem();
      porInstance = task.getPrimaryObjRef().getSystemInstance();
      porType = task.getPrimaryObjRef().getType();
      porValue = task.getPrimaryObjRef().getValue();
    }
  }

  public TaskEvent(String id, TaskSummary task, String userId, String details) {
    super(id, userId, details);
    taskId = task.getId();
    businessProcessId = task.getBusinessProcessId();
    parentBusinessProcessId = task.getParentBusinessProcessId();
    domain = task.getDomain();
    if (task.getWorkbasketSummary() != null) {
      workbasketKey = task.getWorkbasketSummary().getKey();
    }
    if (task.getClassificationSummary() != null) {
      taskClassificationKey = task.getClassificationSummary().getKey();
      taskClassificationCategory = task.getClassificationSummary().getCategory();
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

  @Override
  public String toString() {
    return "TaskEvent [id="
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
