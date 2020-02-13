package pro.taskana.rest.resource;

import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.ResourceSupport;

import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;

/** Resource class for {@link Attachment}. */
public class AttachmentResource extends ResourceSupport {

  private String attachmentId;
  private String taskId;
  private String created;
  private String modified;
  private ClassificationSummaryResource classificationSummaryResource;
  private ObjectReference objectReference;
  private String channel;
  private String received;
  private Map<String, String> customAttributes = new HashMap<String, String>();

  public AttachmentResource() {}

  public AttachmentResource(Attachment attachment) {
    this.attachmentId = attachment.getId();
    this.taskId = attachment.getTaskId();
    this.created = attachment.getCreated() != null ? attachment.getCreated().toString() : null;
    this.modified = attachment.getModified() != null ? attachment.getModified().toString() : null;
    this.classificationSummaryResource =
        new ClassificationSummaryResource(attachment.getClassificationSummary());
    this.objectReference = attachment.getObjectReference();
    this.channel = attachment.getChannel();
    this.received = attachment.getReceived() != null ? attachment.getReceived().toString() : null;
    this.customAttributes = attachment.getCustomAttributes();
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

  public String getReceived() {
    return received;
  }

  public void setReceived(String received) {
    this.received = received;
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

  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }

  @Override
  public String toString() {
    return "AttachmentResource ["
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
