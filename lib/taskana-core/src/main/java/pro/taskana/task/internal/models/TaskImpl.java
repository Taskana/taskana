package pro.taskana.task.internal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

public class TaskImpl extends TaskSummaryImpl implements Task {

  // All objects have to be serializable
  private Map<String, String> customAttributes = new HashMap<>();
  private Map<String, String> callbackInfo = new HashMap<>();
  private CallbackState callbackState;
  private List<Attachment> attachments = new ArrayList<>();

  public TaskImpl() {}

  private TaskImpl(TaskImpl copyFrom) {
    super(copyFrom);
    customAttributes = new HashMap<>(copyFrom.customAttributes);
    callbackInfo = new HashMap<>(copyFrom.callbackInfo);
    callbackState = copyFrom.callbackState;
    attachments = copyFrom.attachments.stream().map(Attachment::copy).collect(Collectors.toList());
  }

  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }

  public String getClassificationId() {
    return classificationSummary == null ? null : classificationSummary.getId();
  }

  @Override
  public TaskImpl copy() {
    return new TaskImpl(this);
  }

  public String getClassificationKey() {
    return classificationSummary == null ? null : classificationSummary.getKey();
  }

  @Override
  public void setClassificationKey(String classificationKey) {
    if (this.classificationSummary == null) {
      this.classificationSummary = new ClassificationSummaryImpl();
    }

    ((ClassificationSummaryImpl) this.classificationSummary).setKey(classificationKey);
  }

  @Override
  public String getWorkbasketKey() {
    return workbasketSummary == null ? null : workbasketSummary.getKey();
  }

  public void setWorkbasketKey(String workbasketKey) {
    if (workbasketSummary == null) {
      workbasketSummary = new WorkbasketSummaryImpl();
    }
    ((WorkbasketSummaryImpl) this.workbasketSummary).setKey(workbasketKey);
  }

  @Override
  public Map<String, String> getCustomAttributeMap() {
    return getCustomAttributes();
  }

  @Override
  public void setCustomAttributeMap(Map<String, String> customAttributes) {
    setCustomAttributes(customAttributes);
  }

  public CallbackState getCallbackState() {
    return callbackState;
  }

  public void setCallbackState(CallbackState callbackState) {
    this.callbackState = callbackState;
  }

  @Override
  public Map<String, String> getCallbackInfo() {
    if (callbackInfo == null) {
      callbackInfo = new HashMap<>();
    }
    return callbackInfo;
  }

  @Override
  public void setCallbackInfo(Map<String, String> callbackInfo) {
    this.callbackInfo = callbackInfo;
  }

  @Deprecated
  @Override
  public void setCustomAttribute(TaskCustomField customField, String value) {
    setCustomField(customField, value);
  }

  @Override
  public void setCustomField(TaskCustomField customField, String value) {
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
      case CUSTOM_5:
        custom5 = value;
        break;
      case CUSTOM_6:
        custom6 = value;
        break;
      case CUSTOM_7:
        custom7 = value;
        break;
      case CUSTOM_8:
        custom8 = value;
        break;
      case CUSTOM_9:
        custom9 = value;
        break;
      case CUSTOM_10:
        custom10 = value;
        break;
      case CUSTOM_11:
        custom11 = value;
        break;
      case CUSTOM_12:
        custom12 = value;
        break;
      case CUSTOM_13:
        custom13 = value;
        break;
      case CUSTOM_14:
        custom14 = value;
        break;
      case CUSTOM_15:
        custom15 = value;
        break;
      case CUSTOM_16:
        custom16 = value;
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }

  @Override
  public void addAttachment(Attachment attachmentToAdd) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    if (attachmentToAdd != null) {
      if (attachmentToAdd.getId() != null) {
        attachments.removeIf(attachment -> attachmentToAdd.getId().equals(attachment.getId()));
      }
      attachments.add(attachmentToAdd);
    }
  }

  @Override
  public List<Attachment> getAttachments() {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    if (attachments != null) {
      this.attachments = attachments;
    } else if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
  }

  @Override
  public TaskSummary asSummary() {
    TaskSummaryImpl taskSummary = new TaskSummaryImpl();
    List<AttachmentSummary> attSummaries = new ArrayList<>();
    for (Attachment att : attachments) {
      attSummaries.add(att.asSummary());
    }
    taskSummary.setAttachmentSummaries(attSummaries);
    taskSummary.setSecondaryObjectReferences(secondaryObjectReferences);
    taskSummary.setBusinessProcessId(this.businessProcessId);
    taskSummary.setClaimed(claimed);
    if (classificationSummary != null) {
      taskSummary.setClassificationSummary(classificationSummary);
    }
    taskSummary.setExternalId(externalId);
    taskSummary.setCompleted(completed);
    taskSummary.setCreated(created);
    taskSummary.setCustom1(custom1);
    taskSummary.setCustom2(custom2);
    taskSummary.setCustom3(custom3);
    taskSummary.setCustom4(custom4);
    taskSummary.setCustom5(custom5);
    taskSummary.setCustom6(custom6);
    taskSummary.setCustom7(custom7);
    taskSummary.setCustom8(custom8);
    taskSummary.setCustom9(custom9);
    taskSummary.setCustom10(custom10);
    taskSummary.setCustom11(custom11);
    taskSummary.setCustom12(custom12);
    taskSummary.setCustom13(custom13);
    taskSummary.setCustom14(custom14);
    taskSummary.setCustom15(custom15);
    taskSummary.setCustom16(custom16);
    taskSummary.setDue(due);
    taskSummary.setId(id);
    taskSummary.setModified(modified);
    taskSummary.setName(name);
    taskSummary.setCreator(creator);
    taskSummary.setNote(note);
    taskSummary.setDescription(description);
    taskSummary.setOwner(owner);
    taskSummary.setParentBusinessProcessId(parentBusinessProcessId);
    taskSummary.setPlanned(planned);
    taskSummary.setReceived(received);
    taskSummary.setPrimaryObjRef(primaryObjRef);
    taskSummary.setPriority(priority);
    taskSummary.setRead(isRead);
    taskSummary.setState(state);
    taskSummary.setTransferred(isTransferred);
    taskSummary.setWorkbasketSummary(workbasketSummary);
    return taskSummary;
  }

  @Override
  public Attachment removeAttachment(String attachmentId) {
    Attachment result = null;
    for (Attachment attachment : attachments) {
      if (attachment.getId().equals(attachmentId) && attachments.remove(attachment)) {
        result = attachment;
        break;
      }
    }
    return result;
  }

  @Override
  public String getClassificationCategory() {
    return this.classificationSummary == null ? null : this.classificationSummary.getCategory();
  }

  @SuppressWarnings("unused")
  public void setClassificationCategory(String classificationCategory) {
    if (this.classificationSummary == null) {
      this.classificationSummary = new ClassificationSummaryImpl();
    }
    ((ClassificationSummaryImpl) this.classificationSummary).setCategory(classificationCategory);
  }

  @Override
  protected boolean canEqual(Object other) {
    return (other instanceof TaskImpl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), id, customAttributes, callbackInfo, callbackState, attachments);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TaskImpl)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    TaskImpl other = (TaskImpl) obj;
    if (!other.canEqual(this)) {
      return false;
    }
    return Objects.equals(id, other.id)
        && Objects.equals(customAttributes, other.customAttributes)
        && Objects.equals(callbackInfo, other.callbackInfo)
        && callbackState == other.callbackState
        && Objects.equals(attachments, other.attachments);
  }

  @Override
  public String toString() {
    return "TaskImpl [customAttributes="
        + customAttributes
        + ", callbackInfo="
        + callbackInfo
        + ", callbackState="
        + callbackState
        + ", attachments="
        + attachments
        + ", objectReferences="
        + secondaryObjectReferences
        + ", id="
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
        + ", primaryObjRef="
        + primaryObjRef
        + ", isRead="
        + isRead
        + ", isTransferred="
        + isTransferred
        + ", attachmentSummaries="
        + attachmentSummaries
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
