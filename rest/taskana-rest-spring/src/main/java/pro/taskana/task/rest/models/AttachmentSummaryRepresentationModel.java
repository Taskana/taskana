package pro.taskana.task.rest.models;

import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;

/** EntityModel class for {@link AttachmentSummary}. */
public class AttachmentSummaryRepresentationModel
    extends RepresentationModel<AttachmentSummaryRepresentationModel> {

  protected String attachmentId;
  protected String taskId;
  protected Instant created;
  protected Instant modified;
  protected Instant received;
  protected ClassificationSummaryRepresentationModel classificationSummary;
  protected ObjectReference objectReference;
  protected String channel;

  // TODO: remove this constructor
  public AttachmentSummaryRepresentationModel() {}

  // TODO: remove this constructor
  public AttachmentSummaryRepresentationModel(AttachmentSummary attachmentSummary) {
    this.attachmentId = attachmentSummary.getId();
    this.taskId = attachmentSummary.getTaskId();
    this.created = attachmentSummary.getCreated();
    this.modified = attachmentSummary.getModified();
    this.received = attachmentSummary.getReceived();
    this.classificationSummary =
        new ClassificationSummaryRepresentationModel(attachmentSummary.getClassificationSummary());
    this.objectReference = attachmentSummary.getObjectReference();
    this.channel = attachmentSummary.getChannel();
  }

  public String getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  public Instant getReceived() {
    return received;
  }

  public void setReceived(Instant received) {
    this.received = received;
  }

  public ClassificationSummaryRepresentationModel getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(
      ClassificationSummaryRepresentationModel classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  public ObjectReference getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReference objectReference) {
    this.objectReference = objectReference;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }
}
