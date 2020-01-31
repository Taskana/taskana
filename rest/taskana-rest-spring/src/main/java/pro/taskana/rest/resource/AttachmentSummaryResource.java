package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.task.api.AttachmentSummary;
import pro.taskana.task.api.ObjectReference;

/** Resource class for {@link AttachmentSummary}. */
public class AttachmentSummaryResource extends ResourceSupport {

  private String attachmentId;
  private String taskId;
  private String created;
  private String modified;
  private ClassificationSummaryResource classificationSummaryResource;
  private ObjectReference objectReference;
  private String channel;
  private String received;

  AttachmentSummaryResource() {}

  public AttachmentSummaryResource(AttachmentSummary attachmentSummary) {
    this.attachmentId = attachmentSummary.getId();
    this.taskId = attachmentSummary.getTaskId();
    this.created =
        attachmentSummary.getCreated() != null ? attachmentSummary.getCreated().toString() : null;
    this.modified =
        attachmentSummary.getModified() != null ? attachmentSummary.getModified().toString() : null;
    this.classificationSummaryResource =
        new ClassificationSummaryResource(attachmentSummary.getClassificationSummary());
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

  public ClassificationSummaryResource getClassificationSummary() {
    return classificationSummaryResource;
  }

  public void setClassificationSummary(
      ClassificationSummaryResource classificationSummaryResource) {
    this.classificationSummaryResource = classificationSummaryResource;
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
        + this.classificationSummaryResource
        + "objectReference= "
        + this.objectReference
        + "channel= "
        + this.channel
        + "received= "
        + this.received
        + "]";
  }
}
