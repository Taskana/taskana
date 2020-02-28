package pro.taskana.task.internal.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** Task entity. */
public class TaskImpl extends TaskSummaryImpl implements Task {

  private static final String NOT_A_VALID_NUMBER_SET =
      "Argument '%s' of setCustomAttribute() cannot be converted to a number between 1 and 16";
  // All objects have to be serializable
  private Map<String, String> customAttributes = Collections.emptyMap();
  private Map<String, String> callbackInfo = Collections.emptyMap();
  private CallbackState callbackState;
  private List<Attachment> attachments = new ArrayList<>();

  public TaskImpl() {}

  public CallbackState getCallbackState() {
    return callbackState;
  }

  public void setCallbackState(CallbackState callbackState) {
    this.callbackState = callbackState;
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
  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  @Override
  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
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

  @Override
  public void setCustomAttribute(String number, String value) throws InvalidArgumentException {
    int num;
    try {
      num = Integer.parseInt(number);
    } catch (NumberFormatException e) {
      throw new InvalidArgumentException(
          String.format(NOT_A_VALID_NUMBER_SET, number), e.getCause());
    }

    switch (num) {
      case 1:
        custom1 = value;
        break;
      case 2:
        custom2 = value;
        break;
      case 3:
        custom3 = value;
        break;
      case 4:
        custom4 = value;
        break;
      case 5:
        custom5 = value;
        break;
      case 6:
        custom6 = value;
        break;
      case 7:
        custom7 = value;
        break;
      case 8:
        custom8 = value;
        break;
      case 9:
        custom9 = value;
        break;
      case 10:
        custom10 = value;
        break;
      case 11:
        custom11 = value;
        break;
      case 12:
        custom12 = value;
        break;
      case 13:
        custom13 = value;
        break;
      case 14:
        custom14 = value;
        break;
      case 15:
        custom15 = value;
        break;
      case 16:
        custom16 = value;
        break;
      default:
        throw new InvalidArgumentException(String.format(NOT_A_VALID_NUMBER_SET, number));
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

  @Override
  public TaskSummary asSummary() {
    TaskSummaryImpl taskSummary = new TaskSummaryImpl();
    List<AttachmentSummary> attSummaries = new ArrayList<>();
    for (Attachment att : attachments) {
      attSummaries.add(att.asSummary());
    }
    taskSummary.setAttachmentSummaries(attSummaries);
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

  public void setAttachments(List<Attachment> attachments) {
    if (attachments != null) {
      this.attachments = attachments;
    } else if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
  }

  public String getClassificationId() {
    return classificationSummary == null ? null : classificationSummary.getId();
  }

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
    return "TaskImpl [id="
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
        + ", due="
        + due
        + ", name="
        + name
        + ", creator="
        + creator
        + ", description="
        + description
        + ", note="
        + note
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
        + ", customAttributes="
        + customAttributes
        + ", callbackInfo="
        + callbackInfo
        + ", callbackState="
        + callbackState
        + ", attachments="
        + attachments
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
