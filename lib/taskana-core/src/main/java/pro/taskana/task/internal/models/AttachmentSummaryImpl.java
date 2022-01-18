package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;

/** The most important fields of the Attachment entity. */
public class AttachmentSummaryImpl implements AttachmentSummary {

  protected String id;
  protected String taskId;
  protected Instant created;
  protected Instant modified;
  protected ClassificationSummary classificationSummary;
  protected ObjectReference objectReference;
  protected String channel;
  protected Instant received;

  AttachmentSummaryImpl() {}

  protected AttachmentSummaryImpl(AttachmentSummaryImpl copyFrom) {
    created = copyFrom.created;
    modified = copyFrom.modified;
    classificationSummary = copyFrom.classificationSummary;
    objectReference = copyFrom.objectReference;
    channel = copyFrom.channel;
    received = copyFrom.received;
  }

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
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public Instant getModified() {
    return modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setModified(Instant modified) {
    this.modified = modified != null ? modified.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public ObjectReference getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReference objectReference) {
    this.objectReference = objectReference;
  }

  @Override
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  @Override
  public ClassificationSummary getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(ClassificationSummary classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  @Override
  public Instant getReceived() {
    return received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setReceived(Instant received) {
    this.received = received != null ? received.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  @Override
  public AttachmentSummaryImpl copy() {
    return new AttachmentSummaryImpl(this);
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  @SuppressWarnings("unused")
  public ClassificationSummaryImpl getClassificationSummaryImpl() {
    return (ClassificationSummaryImpl) classificationSummary;
  }

  // auxiliary method to enable MyBatis access to classificationSummary
  @SuppressWarnings("unused")
  public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  // auxiliary method to enable MyBatis access to objectReference
  public ObjectReferenceImpl getObjectReferenceImpl() {
    return (ObjectReferenceImpl) objectReference;
  }

  // auxiliary method to enable MyBatis access to objectReference
  public void setObjectReferenceImpl(ObjectReferenceImpl objectReferenceImpl) {
    this.objectReference = objectReferenceImpl;
  }

  protected boolean canEqual(Object other) {
    return (!(other instanceof AttachmentSummaryImpl));
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, taskId, created, modified, classificationSummary, objectReference, channel, received);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AttachmentSummaryImpl)) {
      return false;
    }
    AttachmentSummaryImpl other = (AttachmentSummaryImpl) obj;
    if (other.canEqual(this)) {
      return false;
    }
    return Objects.equals(id, other.id)
        && Objects.equals(taskId, other.taskId)
        && Objects.equals(created, other.created)
        && Objects.equals(modified, other.modified)
        && Objects.equals(classificationSummary, other.classificationSummary)
        && Objects.equals(objectReference, other.objectReference)
        && Objects.equals(channel, other.channel)
        && Objects.equals(received, other.received);
  }

  @Override
  public String toString() {
    return "AttachmentSummaryImpl [id="
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
        + "]";
  }
}
