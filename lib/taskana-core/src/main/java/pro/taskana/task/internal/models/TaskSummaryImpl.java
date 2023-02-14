package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** Entity which contains the most important information about a Task. */
@NoArgsConstructor
@EqualsAndHashCode()
@ToString
public class TaskSummaryImpl implements TaskSummary {

  @Getter @Setter protected String id;
  @Getter @Setter protected String externalId;
  protected Instant received;
  protected Instant created;
  protected Instant claimed;
  protected Instant modified;
  protected Instant planned;
  protected Instant due;
  protected Instant completed;
  @Getter protected String name;
  @Getter @Setter protected String creator;
  @Getter protected String note;
  @Getter protected String description;
  @Getter @Setter protected int priority;
  @Getter protected int manualPriority = DEFAULT_MANUAL_PRIORITY;
  @Getter @Setter protected TaskState state;
  @Getter @Setter protected ClassificationSummary classificationSummary;
  @Getter @Setter protected WorkbasketSummary workbasketSummary;
  @Getter @Setter protected String businessProcessId;
  @Getter @Setter protected String parentBusinessProcessId;
  @Getter @Setter protected String owner;
  @Getter protected String ownerLongName;
  @Getter @Setter protected ObjectReference primaryObjRef;
  @Getter @Setter protected boolean isRead;
  @Getter @Setter protected boolean isTransferred;
  // All objects have to be serializable
  @Getter @Setter protected List<AttachmentSummary> attachmentSummaries = new ArrayList<>();
  @Getter @Setter protected List<ObjectReference> secondaryObjectReferences = new ArrayList<>();
  @Getter protected String custom1;
  @Getter protected String custom2;
  @Getter protected String custom3;
  @Getter protected String custom4;
  @Getter protected String custom5;
  @Getter protected String custom6;
  @Getter protected String custom7;
  @Getter protected String custom8;
  @Getter protected String custom9;
  @Getter protected String custom10;
  @Getter protected String custom11;
  @Getter protected String custom12;
  @Getter protected String custom13;
  @Getter protected String custom14;
  @Getter protected String custom15;
  @Getter protected String custom16;
  @Getter @Setter protected Integer customInt1;
  @Getter @Setter protected Integer customInt2;
  @Getter @Setter protected Integer customInt3;
  @Getter @Setter protected Integer customInt4;
  @Getter @Setter protected Integer customInt5;
  @Getter @Setter protected Integer customInt6;
  @Getter @Setter protected Integer customInt7;
  @Getter @Setter protected Integer customInt8;

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
    manualPriority = copyFrom.manualPriority;
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
    customInt1 = copyFrom.customInt1;
    customInt2 = copyFrom.customInt2;
    customInt3 = copyFrom.customInt3;
    customInt4 = copyFrom.customInt4;
    customInt5 = copyFrom.customInt5;
    customInt6 = copyFrom.customInt6;
    customInt7 = copyFrom.customInt7;
    customInt8 = copyFrom.customInt8;
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

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public void setNote(String note) {
    this.note = note == null ? null : note.trim();
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public void setManualPriority(int manualPriority) {
    this.manualPriority = manualPriority;
    if (isManualPriorityActive()) {
      this.priority = manualPriority;
    }
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

  public void setOwnerLongName(String ownerLongName) {
    this.ownerLongName = ownerLongName == null ? null : ownerLongName.trim();
  }

  public void setPrimaryObjRef(
      String company, String system, String systemInstance, String type, String value) {
    this.primaryObjRef = new ObjectReferenceImpl(company, system, systemInstance, type, value);
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
  public Integer getCustomIntField(TaskCustomIntField customIntField) {

    switch (customIntField) {
      case CUSTOM_INT_1:
        return customInt1;
      case CUSTOM_INT_2:
        return customInt2;
      case CUSTOM_INT_3:
        return customInt3;
      case CUSTOM_INT_4:
        return customInt4;
      case CUSTOM_INT_5:
        return customInt5;
      case CUSTOM_INT_6:
        return customInt6;
      case CUSTOM_INT_7:
        return customInt7;
      case CUSTOM_INT_8:
        return customInt8;
      default:
        throw new SystemException("Unknown custom int field '" + customIntField + "'");
    }
  }

  @Override
  public boolean isManualPriorityActive() {
    return manualPriority >= 0;
  }

  @Override
  public TaskSummaryImpl copy() {
    return new TaskSummaryImpl(this);
  }

  // auxiliary method to allow mybatis access to workbasketSummary
  @SuppressWarnings("unused")
  public WorkbasketSummaryImpl getWorkbasketSummaryImpl() {
    return (WorkbasketSummaryImpl) workbasketSummary;
  }

  // auxiliary method to allow mybatis access to workbasketSummary
  @SuppressWarnings("unused")
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
  @SuppressWarnings("unused")
  public ClassificationSummaryImpl getClassificationSummaryImpl() {
    return (ClassificationSummaryImpl) classificationSummary;
  }

  // auxiliary Method to enable Mybatis to access classificationSummary
  @SuppressWarnings("unused")
  public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
    setClassificationSummary(classificationSummary);
  }

  // auxiliary Method to enable Mybatis to access primaryObjRef
  @SuppressWarnings("unused")
  public ObjectReferenceImpl getPrimaryObjRefImpl() {
    return (ObjectReferenceImpl) primaryObjRef;
  }

  // auxiliary Method to enable Mybatis to access primaryObjRef
  @SuppressWarnings("unused")
  public void setPrimaryObjRefImpl(ObjectReferenceImpl objectReference) {
    setPrimaryObjRef(objectReference);
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1 == null ? null : custom1.trim();
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2 == null ? null : custom2.trim();
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3 == null ? null : custom3.trim();
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4 == null ? null : custom4.trim();
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5 == null ? null : custom5.trim();
  }

  public void setCustom6(String custom6) {
    this.custom6 = custom6 == null ? null : custom6.trim();
  }

  public void setCustom7(String custom7) {
    this.custom7 = custom7 == null ? null : custom7.trim();
  }

  public void setCustom8(String custom8) {
    this.custom8 = custom8 == null ? null : custom8.trim();
  }

  public void setCustom9(String custom9) {
    this.custom9 = custom9 == null ? null : custom9.trim();
  }

  public void setCustom10(String custom10) {
    this.custom10 = custom10 == null ? null : custom10.trim();
  }

  public void setCustom11(String custom11) {
    this.custom11 = custom11 == null ? null : custom11.trim();
  }

  public void setCustom12(String custom12) {
    this.custom12 = custom12 == null ? null : custom12.trim();
  }

  public void setCustom13(String custom13) {
    this.custom13 = custom13 == null ? null : custom13.trim();
  }

  public void setCustom14(String custom14) {
    this.custom14 = custom14 == null ? null : custom14.trim();
  }

  public void setCustom15(String custom15) {
    this.custom15 = custom15 == null ? null : custom15.trim();
  }

  public void setCustom16(String custom16) {
    this.custom16 = custom16 == null ? null : custom16.trim();
  }
}
