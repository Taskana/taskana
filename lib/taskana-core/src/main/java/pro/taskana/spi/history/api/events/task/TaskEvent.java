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

  @Override
  public String toString() {
    return "TaskEvent [taskId= "
        + this.taskId
        + ", businessProcessId= "
        + this.businessProcessId
        + ", parentBusinessProcessId= "
        + this.parentBusinessProcessId
        + ", domain= "
        + this.domain
        + ", workbasketKey= "
        + this.workbasketKey
        + ", details= "
        + this.details
        + "]";
  }
}
