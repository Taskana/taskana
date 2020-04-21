package pro.taskana.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** EntityModel class for {@link WorkbasketSummary}. */
@Relation(collectionRelation = "tasks")
public class TaskSummaryResource extends RepresentationModel<TaskSummaryResource> {

  private String taskId;
  private String externalId;
  private String created; // ISO-8601
  private String claimed; // ISO-8601
  private String completed; // ISO-8601
  private String modified; // ISO-8601
  private String planned; // ISO-8601
  private String due; // ISO-8601
  private String name;
  private String creator;
  private String note;
  private String description;
  private int priority;
  private TaskState state;
  private ClassificationSummaryResource classificationSummaryResource;
  private WorkbasketSummaryResource workbasketSummaryResource;
  private String businessProcessId;
  private String parentBusinessProcessId;
  private String owner;
  private ObjectReference primaryObjRef;
  private boolean isRead;
  private boolean isTransferred;
  private List<AttachmentSummaryResource> attachmentSummaryResources = new ArrayList<>();
  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String custom5;
  private String custom6;
  private String custom7;
  private String custom8;
  private String custom9;
  private String custom10;
  private String custom11;
  private String custom12;
  private String custom13;
  private String custom14;
  private String custom15;
  private String custom16;

  TaskSummaryResource() {}

  public TaskSummaryResource(TaskSummary taskSummary) throws InvalidArgumentException {
    this.taskId = taskSummary.getId();
    this.externalId = taskSummary.getExternalId();
    created = taskSummary.getCreated() != null ? taskSummary.getCreated().toString() : null;
    claimed = taskSummary.getClaimed() != null ? taskSummary.getClaimed().toString() : null;
    completed = taskSummary.getCompleted() != null ? taskSummary.getCompleted().toString() : null;
    modified = taskSummary.getModified() != null ? taskSummary.getModified().toString() : null;
    planned = taskSummary.getPlanned() != null ? taskSummary.getPlanned().toString() : null;
    due = taskSummary.getDue() != null ? taskSummary.getDue().toString() : null;
    this.name = taskSummary.getName();
    this.creator = taskSummary.getCreator();
    this.note = taskSummary.getNote();
    this.description = taskSummary.getDescription();
    this.priority = taskSummary.getPriority();
    this.state = taskSummary.getState();
    this.classificationSummaryResource =
        new ClassificationSummaryResource(taskSummary.getClassificationSummary());
    this.workbasketSummaryResource =
        new WorkbasketSummaryResource(taskSummary.getWorkbasketSummary());
    this.businessProcessId = taskSummary.getBusinessProcessId();
    this.parentBusinessProcessId = taskSummary.getParentBusinessProcessId();
    this.owner = taskSummary.getOwner();
    this.primaryObjRef = taskSummary.getPrimaryObjRef();
    this.isRead = taskSummary.isRead();
    this.isTransferred = taskSummary.isTransferred();
    this.attachmentSummaryResources =
        taskSummary.getAttachmentSummaries().stream()
            .map(AttachmentSummaryResource::new)
            .collect(Collectors.toList());
    this.custom1 = taskSummary.getCustomAttribute("1");
    this.custom2 = taskSummary.getCustomAttribute("2");
    this.custom3 = taskSummary.getCustomAttribute("3");
    this.custom4 = taskSummary.getCustomAttribute("4");
    this.custom5 = taskSummary.getCustomAttribute("5");
    this.custom6 = taskSummary.getCustomAttribute("6");
    this.custom7 = taskSummary.getCustomAttribute("7");
    this.custom8 = taskSummary.getCustomAttribute("8");
    this.custom9 = taskSummary.getCustomAttribute("9");
    this.custom10 = taskSummary.getCustomAttribute("10");
    this.custom11 = taskSummary.getCustomAttribute("11");
    this.custom12 = taskSummary.getCustomAttribute("12");
    this.custom13 = taskSummary.getCustomAttribute("13");
    this.custom14 = taskSummary.getCustomAttribute("14");
    this.custom15 = taskSummary.getCustomAttribute("15");
    this.custom16 = taskSummary.getCustomAttribute("16");
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getClaimed() {
    return claimed;
  }

  public void setClaimed(String claimed) {
    this.claimed = claimed;
  }

  public String getCompleted() {
    return completed;
  }

  public void setCompleted(String completed) {
    this.completed = completed;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getPlanned() {
    return planned;
  }

  public void setPlanned(String planned) {
    this.planned = planned;
  }

  public String getDue() {
    return due;
  }

  public void setDue(String due) {
    this.due = due;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  public ClassificationSummaryResource getClassificationSummaryResource() {
    return classificationSummaryResource;
  }

  public void setClassificationSummaryResource(
      ClassificationSummaryResource classificationSummaryResource) {
    this.classificationSummaryResource = classificationSummaryResource;
  }

  public WorkbasketSummaryResource getWorkbasketSummaryResource() {
    return workbasketSummaryResource;
  }

  public void setWorkbasketSummaryResource(WorkbasketSummaryResource workbasketSummaryResource) {
    this.workbasketSummaryResource = workbasketSummaryResource;
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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public ObjectReference getPrimaryObjRef() {
    return primaryObjRef;
  }

  public void setPrimaryObjRef(ObjectReference primaryObjRef) {
    this.primaryObjRef = primaryObjRef;
  }

  public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean isRead) {
    this.isRead = isRead;
  }

  public boolean isTransferred() {
    return isTransferred;
  }

  public void setTransferred(boolean isTransferred) {
    this.isTransferred = isTransferred;
  }

  public List<AttachmentSummaryResource> getAttachmentSummaries() {
    return attachmentSummaryResources;
  }

  public void setAttachmentSummaries(List<AttachmentSummaryResource> attachmentSummaryResources) {
    this.attachmentSummaryResources = attachmentSummaryResources;
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

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  public String getCustom6() {
    return custom6;
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6;
  }

  public String getCustom7() {
    return custom7;
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7;
  }

  public String getCustom8() {
    return custom8;
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8;
  }

  public String getCustom9() {
    return custom9;
  }

  public void setCustom9(String custom9) {
    this.custom9 = custom9;
  }

  public String getCustom10() {
    return custom10;
  }

  public void setCustom10(String custom10) {
    this.custom10 = custom10;
  }

  public String getCustom11() {
    return custom11;
  }

  public void setCustom11(String custom11) {
    this.custom11 = custom11;
  }

  public String getCustom12() {
    return custom12;
  }

  public void setCustom12(String custom12) {
    this.custom12 = custom12;
  }

  public String getCustom13() {
    return custom13;
  }

  public void setCustom13(String custom13) {
    this.custom13 = custom13;
  }

  public String getCustom14() {
    return custom14;
  }

  public void setCustom14(String custom14) {
    this.custom14 = custom14;
  }

  public String getCustom15() {
    return custom15;
  }

  public void setCustom15(String custom15) {
    this.custom15 = custom15;
  }

  public String getCustom16() {
    return custom16;
  }

  public void setCustom16(String custom16) {
    this.custom16 = custom16;
  }

  @Override
  public String toString() {
    return "TaskSummaryResource ["
        + "taskId= "
        + this.taskId
        + "externalId= "
        + this.externalId
        + "created= "
        + this.created
        + "modified= "
        + this.modified
        + "claimed= "
        + this.claimed
        + "completed= "
        + this.completed
        + "planned= "
        + this.planned
        + "due= "
        + this.due
        + "name= "
        + this.name
        + "creator= "
        + this.creator
        + "priority= "
        + this.priority
        + "owner= "
        + this.owner
        + "]";
  }
}
