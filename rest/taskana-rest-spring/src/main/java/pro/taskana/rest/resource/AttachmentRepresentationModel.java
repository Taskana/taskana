package pro.taskana.rest.resource;

import java.util.HashMap;
import java.util.Map;

import pro.taskana.task.api.models.Attachment;

/**
 * EntityModel class for {@link Attachment}.
 */
public class AttachmentRepresentationModel
    extends AttachmentSummaryRepresentationModel {
  
  private Map<String, String> customAttributes = new HashMap<>();

  public AttachmentRepresentationModel() {
  }

  public AttachmentRepresentationModel(Attachment attachment) {
    super(attachment);
    this.customAttributes = attachment.getCustomAttributes();
  }

  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }

  @Override
  public String toString() {
    return "AttachmentRepresentationModel [customAttributes="
               + customAttributes
               + ", attachmentId="
               + attachmentId
               + ", taskId="
               + taskId
               + ", created="
               + created
               + ", modified="
               + modified
               + ", classificationSummaryRepresentationModel="
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
