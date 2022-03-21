package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** Entity which contains the most important information about a Task. */
public class TaskSummaryImpl implements TaskSummary {

  protected String id;
  protected String externalId;
  protected Instant received;
  protected Instant created;
  protected Instant claimed;
  protected Instant modified;
  protected Instant planned;
  protected Instant due;
  protected Instant completed;
  protected String name;
  protected String creator;
  protected String note;
  protected String description;
  protected int priority;
  protected TaskState state;
  protected ClassificationSummary classificationSummary;
  protected WorkbasketSummary workbasketSummary;
  protected String businessProcessId;
  protected String parentBusinessProcessId;
  protected String owner;
  protected String ownerLongName;
  protected ObjectReference primaryObjRef;
  protected boolean isRead;
  protected boolean isTransferred;
  // All objects have to be serializable
  protected List<AttachmentSummary> attachmentSummaries = new ArrayList<>();
  protected List<ObjectReference> secondaryObjectReferences = new ArrayList<>();
  protected String custom1;
  protected String custom2;
  protected String custom3;
  protected String custom4;
  protected String custom5;
  protected String custom6;
  protected String custom7;
  protected String custom8;
  protected String custom9;
  protected String custom10;
  protected String custom11;
  protected String custom12;
  protected String custom13;
  protected String custom14;
  protected String custom15;
  protected String custom16;

  public TaskSummaryImpl() {}

  protected TaskSummaryImpl(TaskSummaryImpl copyFrom) {
    received = copyFrom.received;
    created = copyFrom.created;
    claimed = copyFrom.claimed;
    completed = copyFrom.completed;
    modified = copyFrom.modified;
    planned = copyFrom.planned;
    due = copyFrom.due;
    name = copyFrom.name;
    creator = copyFrom.creator;
    note = copyFrom.note;
    description = copyFrom.description;
    priority = copyFrom.priority;
    state = copyFrom.state;
    classificationSummary = copyFrom.classificationSummary;
    workbasketSummary = copyFrom.workbasketSummary;
    businessProcessId = copyFrom.businessProcessId;
    parentBusinessProcessId = copyFrom.parentBusinessProcessId;
    owner = copyFrom.owner;
    ownerLongName = copyFrom.ownerLongName;
    primaryObjRef = copyFrom.primaryObjRef;
    isRead = copyFrom.isRead;
    isTransferred = copyFrom.isTransferred;
    attachmentSummaries = new ArrayList<>(copyFrom.attachmentSummaries);
    secondaryObjectReferences =
        copyFrom.secondaryObjectReferences.stream()
            .map(ObjectReference::copy)
            .collect(Collectors.toList());
    custom1 = copyFrom.custom1;
    custom2 = copyFrom.custom2;
    custom3 = copyFrom.custom3;
    custom4 = copyFrom.custom4;
    custom5 = copyFrom.custom5;
    custom6 = copyFrom.custom6;
    custom7 = copyFrom.custom7;
    custom8 = copyFrom.custom8;
    custom9 = copyFrom.custom9;
    custom10 = copyFrom.custom10;
    custom11 = copyFrom.custom11;
    custom12 = copyFrom.custom12;
    custom13 = copyFrom.custom13;
    custom14 = copyFrom.custom14;
    custom15 = copyFrom.custom15;
    custom16 = copyFrom.custom16;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  @Override
  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  @Override
  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getClaimed() {
    return claimed != null ? claimed.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setClaimed(Instant claimed) {
    this.claimed = claimed != null ? claimed.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getCompleted() {
    return completed != null ? completed.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCompleted(Instant completed) {
    this.completed = completed != null ? completed.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getPlanned() {
    return planned != null ? planned.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setPlanned(Instant planned) {
    this.planned = planned != null ? planned.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getReceived() {
    return received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setReceived(Instant received) {
    this.received = received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getDue() {
    return due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setDue(Instant due) {
    this.due = due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  @Override
  public ClassificationSummary getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(ClassificationSummary classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  @Override
  public WorkbasketSummary getWorkbasketSummary() {
    return workbasketSummary;
  }

  public void setWorkbasketSummary(WorkbasketSummary workbasketSummary) {
    this.workbasketSummary = workbasketSummary;
  }

  @Override
  public List<AttachmentSummary> getAttachmentSummaries() {
    return attachmentSummaries;
  }

  public void setAttachmentSummaries(List<AttachmentSummary> attachmentSummaries) {
    this.attachmentSummaries = attachmentSummaries;
  }

  @Override
  public List<ObjectReference> getSecondaryObjectReferences() {
    return secondaryObjectReferences;
  }

  public void setSecondaryObjectReferences(List<ObjectReference> objectReferences) {
    this.secondaryObjectReferences = objectReferences;
  }

  @Override
  public String getDomain() {
    return workbasketSummary == null ? null : workbasketSummary.getDomain();
  }

  public void setDomain(String domain) {
    if (workbasketSummary == null) {
      workbasketSummary = new WorkbasketSummaryImpl();
    }
    ((WorkbasketSummaryImpl) this.workbasketSummary).setDomain(domain);
  }

  @Override
  public String getBusinessProcessId() {
    return businessProcessId;
  }

  public void setBusinessProcessId(String businessProcessId) {
    this.businessProcessId = businessProcessId;
  }

  @Override
  public String getParentBusinessProcessId() {
    return parentBusinessProcessId;
  }

  public void setParentBusinessProcessId(String parentBusinessProcessId) {
    this.parentBusinessProcessId = parentBusinessProcessId;
  }

  @Override
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  @Override
  public String getOwnerLongName() {
    return ownerLongName;
  }

  public void setOwnerLongName(String ownerLongName) {
    this.ownerLongName = ownerLongName;
  }

  @Override
  public ObjectReference getPrimaryObjRef() {
    return primaryObjRef;
  }

  public void setPrimaryObjRef(ObjectReference primaryObjRef) {
    this.primaryObjRef = primaryObjRef;
  }

  public void setPrimaryObjRef(
      String company, String system, String systemInstance, String type, String value) {
    this.primaryObjRef = new ObjectReferenceImpl(company, system, systemInstance, type, value);
  }

  @Override
  public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean isRead) {
    this.isRead = isRead;
  }

  @Override
  public boolean isTransferred() {
    return isTransferred;
  }

  public void setTransferred(boolean isTransferred) {
    this.isTransferred = isTransferred;
  }

  @Deprecated
  @Override
  public String getCustomAttribute(TaskCustomField customField) {
    return getCustomField(customField);
  }

  @Override
  public String getCustomField(TaskCustomField customField) {

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
      case CUSTOM_9:
        return custom9;
      case CUSTOM_10:
        return custom10;
      case CUSTOM_11:
        return custom11;
      case CUSTOM_12:
        return custom12;
      case CUSTOM_13:
        return custom13;
      case CUSTOM_14:
        return custom14;
      case CUSTOM_15:
        return custom15;
      case CUSTOM_16:
        return custom16;
      default:
        throw new SystemException("Unknown custom field '" + customField + "'");
    }
  }

  @Override
  public TaskSummaryImpl copy() {
    return new TaskSummaryImpl(this);
  }

  // utility method to allow mybatis access to workbasketSummary
  public WorkbasketSummaryImpl getWorkbasketSummaryImpl() {
    return (WorkbasketSummaryImpl) workbasketSummary;
  }

  // utility method to allow mybatis access to workbasketSummary
  public void setWorkbasketSummaryImpl(WorkbasketSummaryImpl workbasketSummary) {
    setWorkbasketSummary(workbasketSummary);
  }

  public void addAttachmentSummary(AttachmentSummary attachmentSummary) {
    if (this.attachmentSummaries == null) {
      this.attachmentSummaries = new ArrayList<>();
    }
    this.attachmentSummaries.add(attachmentSummary);
  }

  @Override
  public void addSecondaryObjectReference(ObjectReference objectReferenceToAdd) {
    if (secondaryObjectReferences == null) {
      secondaryObjectReferences = new ArrayList<>();
    }
    if (objectReferenceToAdd != null) {
      ((ObjectReferenceImpl) objectReferenceToAdd).setTaskId(this.id);
      if (objectReferenceToAdd.getId() != null) {
        secondaryObjectReferences.removeIf(
            objectReference -> objectReferenceToAdd.getId().equals(objectReference.getId()));
      }
      secondaryObjectReferences.add(objectReferenceToAdd);
    }
  }

  @Override
  public void addSecondaryObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    ObjectReferenceImpl objectReferenceToAdd =
        new ObjectReferenceImpl(company, system, systemInstance, type, value);
    if (secondaryObjectReferences == null) {
      secondaryObjectReferences = new ArrayList<>();
    }
    objectReferenceToAdd.setTaskId(this.id);
    if (objectReferenceToAdd.getId() != null) {
      secondaryObjectReferences.removeIf(
          objectReference -> objectReferenceToAdd.getId().equals(objectReference.getId()));
    }
    secondaryObjectReferences.add(objectReferenceToAdd);
  }

  @Override
  public ObjectReference removeSecondaryObjectReference(String objectReferenceId) {
    ObjectReference result = null;
    for (ObjectReference objectReference : secondaryObjectReferences) {
      if (objectReference.getId().equals(objectReferenceId)
          && secondaryObjectReferences.remove(objectReference)) {
        result = objectReference;
        break;
      }
    }
    return result;
  }

  // auxiliary Method to enable Mybatis to access classificationSummary
  public ClassificationSummaryImpl getClassificationSummaryImpl() {
    return (ClassificationSummaryImpl) classificationSummary;
  }

  // auxiliary Method to enable Mybatis to access classificationSummary
  public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
    setClassificationSummary(classificationSummary);
  }

  // auxiliary Method to enable Mybatis to access primaryObjRef
  public ObjectReferenceImpl getPrimaryObjRefImpl() {
    return (ObjectReferenceImpl) primaryObjRef;
  }

  // auxiliary Method to enable Mybatis to access primaryObjRef
  public void setPrimaryObjRefImpl(ObjectReferenceImpl objectReference) {
    setPrimaryObjRef(objectReference);
  }

  public String getCustom1() {
    return custom1;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  public String getCustom2() {
    return custom2;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  public String getCustom3() {
    return custom3;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  public String getCustom4() {
    return custom4;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  public String getCustom5() {
    return custom5;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  public String getCustom6() {
    return custom6;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom6(String custom6) {
    this.custom6 = custom6;
  }

  public String getCustom7() {
    return custom7;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom7(String custom7) {
    this.custom7 = custom7;
  }

  public String getCustom8() {
    return custom8;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom8(String custom8) {
    this.custom8 = custom8;
  }

  public String getCustom9() {
    return custom9;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom9(String custom9) {
    this.custom9 = custom9;
  }

  public String getCustom10() {
    return custom10;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom10(String custom10) {
    this.custom10 = custom10;
  }

  public String getCustom11() {
    return custom11;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom11(String custom11) {
    this.custom11 = custom11;
  }

  public String getCustom12() {
    return custom12;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom12(String custom12) {
    this.custom12 = custom12;
  }

  public String getCustom13() {
    return custom13;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom13(String custom13) {
    this.custom13 = custom13;
  }

  public String getCustom14() {
    return custom14;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom14(String custom14) {
    this.custom14 = custom14;
  }

  public String getCustom15() {
    return custom15;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom15(String custom15) {
    this.custom15 = custom15;
  }

  public String getCustom16() {
    return custom16;
  }

  // auxiliary Method needed by Mybatis
  public void setCustom16(String custom16) {
    this.custom16 = custom16;
  }

  protected boolean canEqual(Object other) {
    return (other instanceof TaskSummaryImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        externalId,
        created,
        claimed,
        completed,
        modified,
        planned,
        received,
        due,
        name,
        creator,
        note,
        description,
        priority,
        state,
        classificationSummary,
        workbasketSummary,
        businessProcessId,
        parentBusinessProcessId,
        owner,
        ownerLongName,
        primaryObjRef,
        isRead,
        isTransferred,
        attachmentSummaries,
        secondaryObjectReferences,
        custom1,
        custom2,
        custom3,
        custom4,
        custom5,
        custom6,
        custom7,
        custom8,
        custom9,
        custom10,
        custom11,
        custom12,
        custom13,
        custom14,
        custom15,
        custom16);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TaskSummaryImpl)) {
      return false;
    }
    TaskSummaryImpl other = (TaskSummaryImpl) obj;
    if (!other.canEqual(this)) {
      return false;
    }
    return priority == other.priority
        && isRead == other.isRead
        && isTransferred == other.isTransferred
        && Objects.equals(id, other.id)
        && Objects.equals(externalId, other.externalId)
        && Objects.equals(created, other.created)
        && Objects.equals(claimed, other.claimed)
        && Objects.equals(completed, other.completed)
        && Objects.equals(modified, other.modified)
        && Objects.equals(planned, other.planned)
        && Objects.equals(received, other.received)
        && Objects.equals(due, other.due)
        && Objects.equals(name, other.name)
        && Objects.equals(creator, other.creator)
        && Objects.equals(note, other.note)
        && Objects.equals(description, other.description)
        && state == other.state
        && Objects.equals(classificationSummary, other.classificationSummary)
        && Objects.equals(workbasketSummary, other.workbasketSummary)
        && Objects.equals(businessProcessId, other.businessProcessId)
        && Objects.equals(parentBusinessProcessId, other.parentBusinessProcessId)
        && Objects.equals(owner, other.owner)
        && Objects.equals(ownerLongName, other.ownerLongName)
        && Objects.equals(primaryObjRef, other.primaryObjRef)
        && Objects.equals(attachmentSummaries, other.attachmentSummaries)
        && Objects.equals(secondaryObjectReferences, other.secondaryObjectReferences)
        && Objects.equals(custom1, other.custom1)
        && Objects.equals(custom2, other.custom2)
        && Objects.equals(custom3, other.custom3)
        && Objects.equals(custom4, other.custom4)
        && Objects.equals(custom5, other.custom5)
        && Objects.equals(custom6, other.custom6)
        && Objects.equals(custom7, other.custom7)
        && Objects.equals(custom8, other.custom8)
        && Objects.equals(custom9, other.custom9)
        && Objects.equals(custom10, other.custom10)
        && Objects.equals(custom11, other.custom11)
        && Objects.equals(custom12, other.custom12)
        && Objects.equals(custom13, other.custom13)
        && Objects.equals(custom14, other.custom14)
        && Objects.equals(custom15, other.custom15)
        && Objects.equals(custom16, other.custom16);
  }

  @Override
  public String toString() {
    return "TaskSummaryImpl [id="
        + id
        + ", externalId="
        + externalId
        + ", created="
        + created
        + ", claimed="
        + claimed
        + ", completed="
        + completed
        + ", modified="
        + modified
        + ", planned="
        + planned
        + ", received="
        + received
        + ", due="
        + due
        + ", name="
        + name
        + ", creator="
        + creator
        + ", note="
        + note
        + ", description="
        + description
        + ", priority="
        + priority
        + ", state="
        + state
        + ", classificationSummary="
        + classificationSummary
        + ", workbasketSummary="
        + workbasketSummary
        + ", businessProcessId="
        + businessProcessId
        + ", parentBusinessProcessId="
        + parentBusinessProcessId
        + ", owner="
        + owner
        + ", ownerLongName="
        + ownerLongName
        + ", primaryObjRef="
        + primaryObjRef
        + ", isRead="
        + isRead
        + ", isTransferred="
        + isTransferred
        + ", attachmentSummaries="
        + attachmentSummaries
        + ", objectReferences="
        + secondaryObjectReferences
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
        + ", custom9="
        + custom9
        + ", custom10="
        + custom10
        + ", custom11="
        + custom11
        + ", custom12="
        + custom12
        + ", custom13="
        + custom13
        + ", custom14="
        + custom14
        + ", custom15="
        + custom15
        + ", custom16="
        + custom16
        + "]";
  }
}
