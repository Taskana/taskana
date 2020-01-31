package pro.taskana.task.internal;

import java.time.Instant;
import java.util.Objects;

import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.classification.internal.ClassificationSummaryImpl;
import pro.taskana.task.api.AttachmentSummary;
import pro.taskana.task.api.ObjectReference;

/** The most important fields of the Attachment entity. */
public class AttachmentSummaryImpl implements AttachmentSummary {

  private String id;
  private String taskId;
  private Instant created;
  private Instant modified;
  private ClassificationSummary classificationSummary;
  private ObjectReference objectReference;
  private String channel;
  private Instant received;

  AttachmentSummaryImpl() {}

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getId()
   */
  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getTaskId()
   */
  @Override
  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getCreated()
   */
  @Override
  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getModified()
   */
  @Override
  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.AttachmentSummary#getObjectReference()
   */
  @Override
  public ObjectReference getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReference objectReference) {
    this.objectReference = objectReference;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.AttachmentSummary#getChannel()
   */
  @Override
  public String getChannel() {
    return channel;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getClassification()
   */
  @Override
  public ClassificationSummary getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(ClassificationSummary classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  /*
   * (non-Javadoc)
   * @see pro.taskana.impl.AttachmentSummary#getReceived()
   */
  @Override
  public Instant getReceived() {
    return received;
  }

  public void setReceived(Instant received) {
    this.received = received;
  }

  public void setChannel(String channel) {
    this.channel = channel;
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
