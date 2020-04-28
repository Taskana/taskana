package pro.taskana.rest.resource;

import org.springframework.hateoas.RepresentationModel;

import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;

/**
 * EntityModel class for {@link AttachmentSummary}.
 */
public class AttachmentSummaryRepresentationModel
    extends RepresentationModel<AttachmentSummaryRepresentationModel> {

  protected String attachmentId;
  protected String taskId;
  protected String created;
  protected String modified;
  protected ClassificationSummaryRepresentationModel classificationSummary;
  protected ObjectReference objectReference;
  protected String channel;
  protected String received;

  AttachmentSummaryRepresentationModel() {
  }

  public AttachmentSummaryRepresentationModel(AttachmentSummary attachmentSummary) {
    this.attachmentId = attachmentSummary.getId();
    this.taskId = attachmentSummary.getTaskId();
    this.created =
        attachmentSummary.getCreated() != null ? attachmentSummary.getCreated().toString() : null;
    this.modified =
        attachmentSummary.getModified() != null ? attachmentSummary.getModified().toString() : null;
    this.classificationSummary =
        new ClassificationSummaryRepresentationModel(attachmentSummary.getClassificationSummary());
    this.objectReference = attachmentSummary.getObjectReference();
    this.channel = attachmentSummary.getChannel();
    this.received =
        attachmentSummary.getReceived() != null ? attachmentSummary.getReceived().toString() : null;
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

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
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

  public String getReceived() {
    return received;
  }

  public void setReceived(String received) {
    this.received = received;
  }

  @Override
  public String toString() {
    return "AttachmentSummaryResource ["
               + "attachmentId= "
               + this.attachmentId
               + "taskId= "
               + this.taskId
               + "created= "
               + this.created
               + "modified= "
               + this.modified
               + "classificationSummaryResource= "
               + this.classificationSummary
               + "objectReference= "
               + this.objectReference
               + "channel= "
               + this.channel
               + "received= "
               + this.received
               + "]";
  }
}
