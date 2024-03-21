package pro.taskana.task.rest.models;

import static pro.taskana.task.api.models.TaskSummary.DEFAULT_MANUAL_PRIORITY;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

public class TaskSummaryRepresentationModel
    extends RepresentationModel<TaskSummaryRepresentationModel> {

  /** Unique Id. */
  protected String taskId;
  /**
   * External Id. Can be used to enforce idempotence at task creation. Can identify an external
   * task.
   */
  protected String externalId;
  /** The creation timestamp in the system. */
  protected Instant created;
  /** The timestamp of the last claim-operation. */
  protected Instant claimed;
  /** The timestamp of the completion. */
  protected Instant completed;
  /** The timestamp of the last modification. */
  protected Instant modified;
  /**
   * Planned start of the task. The actual completion of the task should be between PLANNED and DUE.
   */
  protected Instant planned;
  /**
   * Timestamp when the task has been received. It notes when the surrounding process started and
   * not just when the actual task was created.
   */
  protected Instant received;
  /**
   * Timestamp when the task is due. The actual completion of the task should be between PLANNED and
   * DUE.
   */
  protected Instant due;
  /** The name of the task. */
  protected String name;
  /** the creator of the task. */
  protected String creator;
  /** note. */
  protected String note;
  /** The description of the task. */
  protected String description;
  /** The priority of the task. */
  protected int priority;
  /**
   * The manual priority of the task. If the value of manualPriority is zero or greater, the
   * priority is automatically set to manualPriority. In this case, all computations of priority are
   * disabled. If the value of manualPriority is negative, Tasks are not prioritized manually.
   */
  protected int manualPriority = DEFAULT_MANUAL_PRIORITY;
  /** The current task state. */
  protected TaskState state;
  /** The current count of the comments. */
  protected int numberOfComments;
  /** The classification of this task. */
  @NotNull protected ClassificationSummaryRepresentationModel classificationSummary;
  /** The workbasket this task resides in. */
  @NotNull protected WorkbasketSummaryRepresentationModel workbasketSummary;
  /** The business process id. */
  protected String businessProcessId;
  /** the parent business process id. */
  protected String parentBusinessProcessId;
  /** The owner of the task. The owner is set upon claiming of the task. */
  protected String owner;
  /** The long name of the task owner. */
  protected String ownerLongName;
  /** The Objects primary ObjectReference. */
  @NotNull protected ObjectReferenceRepresentationModel primaryObjRef;
  /** Indicator if the task has been read. */
  protected boolean isRead;
  /** Indicator if the task has been transferred. */
  protected boolean isTransferred;
  /** Number of Tasks that are grouped together with this Task during a groupBy. */
  protected Integer groupByCount;
  /** A custom property with name "1". */
  protected String custom1;
  /** A custom property with name "2". */
  protected String custom2;
  /** A custom property with name "3". */
  protected String custom3;
  /** A custom property with name "4". */
  protected String custom4;
  /** A custom property with name "5". */
  protected String custom5;
  /** A custom property with name "6". */
  protected String custom6;
  /** A custom property with name "7". */
  protected String custom7;
  /** A custom property with name "8". */
  protected String custom8;
  /** A custom property with name "9". */
  protected String custom9;
  /** A custom property with name "10". */
  protected String custom10;
  /** A custom property with name "11". */
  protected String custom11;
  /** A custom property with name "12". */
  protected String custom12;
  /** A custom property with name "13". */
  protected String custom13;
  /** A custom property with name "14". */
  protected String custom14;
  /** A custom property with name "15". */
  protected String custom15;
  /** A custom property with name "16". */
  protected String custom16;
  /** A custom int property with name "1". */
  protected Integer customInt1;
  /** A custom int property with name "2". */
  protected Integer customInt2;
  /** A custom int property with name "3". */
  protected Integer customInt3;
  /** A custom int property with name "4". */
  protected Integer customInt4;
  /** A custom int property with name "5". */
  protected Integer customInt5;
  /** A custom int property with name "6". */
  protected Integer customInt6;
  /** A custom int property with name "7". */
  protected Integer customInt7;
  /** A custom int property with name "8". */
  protected Integer customInt8;
  /** Secondary object references of the task. */
  protected List<ObjectReferenceRepresentationModel> secondaryObjectReferences = new ArrayList<>();
  /** The attachment summaries of this task. */
  private List<AttachmentSummaryRepresentationModel> attachmentSummaries = new ArrayList<>();

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

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getClaimed() {
    return claimed;
  }

  public void setClaimed(Instant claimed) {
    this.claimed = claimed;
  }

  public Instant getCompleted() {
    return completed;
  }

  public void setCompleted(Instant completed) {
    this.completed = completed;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  public Instant getPlanned() {
    return planned;
  }

  public void setPlanned(Instant planned) {
    this.planned = planned;
  }

  public Instant getReceived() {
    return received;
  }

  public void setReceived(Instant received) {
    this.received = received;
  }

  public Instant getDue() {
    return due;
  }

  public void setDue(Instant due) {
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

  public int getManualPriority() {
    return manualPriority;
  }

  public void setManualPriority(int manualPriority) {
    this.manualPriority = manualPriority;
  }

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  public int getNumberOfComments() {
    return numberOfComments;
  }

  public void setNumberOfComments(int numberOfComments) {
    this.numberOfComments = numberOfComments;
  }

  public ClassificationSummaryRepresentationModel getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(
      ClassificationSummaryRepresentationModel classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  public WorkbasketSummaryRepresentationModel getWorkbasketSummary() {
    return workbasketSummary;
  }

  public void setWorkbasketSummary(WorkbasketSummaryRepresentationModel workbasketSummary) {
    this.workbasketSummary = workbasketSummary;
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

  public String getOwnerLongName() {
    return ownerLongName;
  }

  public void setOwnerLongName(String ownerLongName) {
    this.ownerLongName = ownerLongName;
  }

  public ObjectReferenceRepresentationModel getPrimaryObjRef() {
    return primaryObjRef;
  }

  public void setPrimaryObjRef(ObjectReferenceRepresentationModel primaryObjRef) {
    this.primaryObjRef = primaryObjRef;
  }

  public List<ObjectReferenceRepresentationModel> getSecondaryObjectReferences() {
    return secondaryObjectReferences;
  }

  public void setSecondaryObjectReferences(
      List<ObjectReferenceRepresentationModel> secondaryObjectReferences) {
    this.secondaryObjectReferences = secondaryObjectReferences;
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

  public List<AttachmentSummaryRepresentationModel> getAttachmentSummaries() {
    return attachmentSummaries;
  }

  public void setAttachmentSummaries(
      List<AttachmentSummaryRepresentationModel> attachmentSummaries) {
    this.attachmentSummaries = attachmentSummaries;
  }

  public Integer getGroupByCount() {
    return groupByCount;
  }

  public void setGroupByCount(Integer groupByCount) {
    this.groupByCount = groupByCount;
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

  public Integer getCustomInt1() {
    return customInt1;
  }

  public void setCustomInt1(Integer customInt1) {
    this.customInt1 = customInt1;
  }

  public Integer getCustomInt2() {
    return customInt2;
  }

  public void setCustomInt2(Integer customInt2) {
    this.customInt2 = customInt2;
  }

  public Integer getCustomInt3() {
    return customInt3;
  }

  public void setCustomInt3(Integer customInt3) {
    this.customInt3 = customInt3;
  }

  public Integer getCustomInt4() {
    return customInt4;
  }

  public void setCustomInt4(Integer customInt4) {
    this.customInt4 = customInt4;
  }

  public Integer getCustomInt5() {
    return customInt5;
  }

  public void setCustomInt5(Integer customInt5) {
    this.customInt5 = customInt5;
  }

  public Integer getCustomInt6() {
    return customInt6;
  }

  public void setCustomInt6(Integer customInt6) {
    this.customInt6 = customInt6;
  }

  public Integer getCustomInt7() {
    return customInt7;
  }

  public void setCustomInt7(Integer customInt7) {
    this.customInt7 = customInt7;
  }

  public Integer getCustomInt8() {
    return customInt8;
  }

  public void setCustomInt8(Integer customInt8) {
    this.customInt8 = customInt8;
  }
}
