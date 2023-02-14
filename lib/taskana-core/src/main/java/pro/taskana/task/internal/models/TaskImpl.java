package pro.taskana.task.internal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskImpl extends TaskSummaryImpl implements Task {

  // All objects have to be serializable
  @Getter @Setter private Map<String, String> customAttributes = new HashMap<>();
  @Setter private Map<String, String> callbackInfo = new HashMap<>();
  @Getter @Setter private CallbackState callbackState;
  private List<Attachment> attachments = new ArrayList<>();

  private TaskImpl(TaskImpl copyFrom) {
    super(copyFrom);
    customAttributes = new HashMap<>(copyFrom.customAttributes);
    callbackInfo = new HashMap<>(copyFrom.callbackInfo);
    callbackState = copyFrom.callbackState;
    attachments = copyFrom.attachments.stream().map(Attachment::copy).collect(Collectors.toList());
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

  @Override
  public Map<String, String> getCallbackInfo() {
    if (callbackInfo == null) {
      callbackInfo = new HashMap<>();
    }
    return callbackInfo;
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
  public void setCustomIntField(TaskCustomIntField customIntField, Integer value) {
    switch (customIntField) {
      case CUSTOM_INT_1:
        customInt1 = value;
        break;
      case CUSTOM_INT_2:
        customInt2 = value;
        break;
      case CUSTOM_INT_3:
        customInt3 = value;
        break;
      case CUSTOM_INT_4:
        customInt4 = value;
        break;
      case CUSTOM_INT_5:
        customInt5 = value;
        break;
      case CUSTOM_INT_6:
        customInt6 = value;
        break;
      case CUSTOM_INT_7:
        customInt7 = value;
        break;
      case CUSTOM_INT_8:
        customInt8 = value;
        break;
      default:
        throw new SystemException("Unknown customIntField '" + customIntField + "'");
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
    taskSummary.setCustomInt1(customInt1);
    taskSummary.setCustomInt2(customInt2);
    taskSummary.setCustomInt3(customInt3);
    taskSummary.setCustomInt4(customInt4);
    taskSummary.setCustomInt5(customInt5);
    taskSummary.setCustomInt6(customInt6);
    taskSummary.setCustomInt7(customInt7);
    taskSummary.setCustomInt8(customInt8);
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
    taskSummary.setManualPriority(manualPriority);
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
}
