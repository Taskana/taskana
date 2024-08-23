package io.kadai.task.rest.models;

import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "EntityModel class for AttachmentSummary")
public class AttachmentSummaryRepresentationModel
    extends RepresentationModel<AttachmentSummaryRepresentationModel> {

  @Schema(name = "attachmentId", description = "Unique Id.")
  protected String attachmentId;

  @Schema(name = "taskId", description = "The referenced task id.")
  protected String taskId;

  @Schema(name = "created", description = "The creation timestamp in the system.")
  protected Instant created;

  @Schema(name = "modified", description = "The timestamp of the last modification.")
  protected Instant modified;

  @Schema(name = "received", description = "The timestamp of the entry date.")
  protected Instant received;

  @Schema(name = "classificationSummary", description = "The classification of this attachment.")
  protected ClassificationSummaryRepresentationModel classificationSummary;

  @Schema(name = "objectReference", description = "The Objects primary ObjectReference.")
  protected ObjectReferenceRepresentationModel objectReference;

  @Schema(
      name = "channel",
      description = "Determines on which channel this attachment was received.")
  protected String channel;

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

  public ObjectReferenceRepresentationModel getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReferenceRepresentationModel objectReference) {
    this.objectReference = objectReference;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }
}
