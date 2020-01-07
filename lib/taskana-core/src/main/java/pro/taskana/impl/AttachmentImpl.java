package pro.taskana.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;

/**
 * Attachment entity.
 *
 * @author bbr
 */
public class AttachmentImpl implements Attachment {

  private String id;
  private String taskId;
  private Instant created;
  private Instant modified;
  private ClassificationSummary classificationSummary;
  private ObjectReference objectReference;
  private String channel;
  private Instant received;
  private Map<String, String> customAttributes = new HashMap<String, String>();

  AttachmentImpl() {}

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  @Override
  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  @Override
  public ClassificationSummary getClassificationSummary() {
    return classificationSummary;
  }

  @Override
  public void setClassificationSummary(ClassificationSummary classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  public ClassificationSummaryImpl getClassificationSummaryImpl() {
    return (ClassificationSummaryImpl) classificationSummary;
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  @Override
  public ObjectReference getObjectReference() {
    return objectReference;
  }

  @Override
  public void setObjectReference(ObjectReference objectReference) {
    this.objectReference = objectReference;
  }

  @Override
  public String getChannel() {
    return channel;
  }

  @Override
  public void setChannel(String channel) {
    this.channel = channel;
  }

  @Override
  public Instant getReceived() {
    return received;
  }

  @Override
  public void setReceived(Instant received) {
    this.received = received;
  }

  @Override
  public Map<String, String> getCustomAttributes() {
    if (customAttributes == null) {
      customAttributes = new HashMap<>();
    }
    return customAttributes;
  }

  @Override
  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
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

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        taskId,
        created,
        modified,
        classificationSummary,
        objectReference,
        channel,
        received,
        customAttributes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AttachmentImpl)) {
      return false;
    }
    AttachmentImpl other = (AttachmentImpl) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(taskId, other.taskId)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(classificationSummary, other.classificationSummary)
        && Objects.equals(objectReference, other.objectReference)
        && Objects.equals(channel, other.channel)
        && Objects.equals(received, other.received)
        && Objects.equals(customAttributes, other.customAttributes);
  }

  @Override
  public String toString() {
    return "AttachmentImpl [id="
        + id
        + ", taskId="
        + taskId
        + ", created="
        + created
        + ", modified="
        + modified
        + ", classificationSummary="
        + classificationSummary
        + ", objectReference="
        + objectReference
        + ", channel="
        + channel
        + ", received="
        + received
        + ", customAttributes="
        + customAttributes
        + "]";
  }
}
