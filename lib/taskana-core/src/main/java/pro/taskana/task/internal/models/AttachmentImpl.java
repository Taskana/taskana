package pro.taskana.task.internal.models;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;

/** Attachment entity. */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AttachmentImpl extends AttachmentSummaryImpl implements Attachment {

  private Map<String, String> customAttributes = new HashMap<>();

  private AttachmentImpl(AttachmentImpl copyFrom) {
    super(copyFrom);
    customAttributes = new HashMap<>(copyFrom.customAttributes);
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
  public AttachmentSummary asSummary() {
    AttachmentSummaryImpl summary = new AttachmentSummaryImpl();
    summary.setClassificationSummary(this.classificationSummary);
    summary.setCreated(this.created);
    summary.setId(this.id);
    summary.setModified(this.modified);
    summary.setReceived(this.received);
    summary.setTaskId(this.taskId);
    summary.setChannel(this.channel);
    summary.setObjectReference(this.objectReference);
    return summary;
  }

  public Map<String, String> getCustomAttributes() {
    if (customAttributes == null) {
      customAttributes = new HashMap<>();
    }
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }

  @Override
  public AttachmentImpl copy() {
    return new AttachmentImpl(this);
  }
}
