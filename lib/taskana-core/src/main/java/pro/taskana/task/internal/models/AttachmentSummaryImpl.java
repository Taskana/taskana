package pro.taskana.task.internal.models;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationSummaryImpl;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;

/** The most important fields of the Attachment entity. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class AttachmentSummaryImpl implements AttachmentSummary {

  protected String id;
  protected String taskId;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected Instant created;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected Instant modified;

  protected ClassificationSummary classificationSummary;
  protected ObjectReference objectReference;
  protected String channel;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected Instant received;

  protected AttachmentSummaryImpl(AttachmentSummaryImpl copyFrom) {
    created = copyFrom.created;
    modified = copyFrom.modified;
    classificationSummary = copyFrom.classificationSummary;
    objectReference = copyFrom.objectReference;
    channel = copyFrom.channel;
    received = copyFrom.received;
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
  @SuppressWarnings("unused")
  public ObjectReferenceImpl getObjectReferenceImpl() {
    return (ObjectReferenceImpl) objectReference;
  }

  // auxiliary method to enable MyBatis access to objectReference
  @SuppressWarnings("unused")
  public void setObjectReferenceImpl(ObjectReferenceImpl objectReferenceImpl) {
    this.objectReference = objectReferenceImpl;
  }
}
