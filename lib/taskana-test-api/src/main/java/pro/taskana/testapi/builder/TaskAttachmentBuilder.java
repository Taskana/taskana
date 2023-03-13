package pro.taskana.testapi.builder;

import java.time.Instant;
import java.util.Map;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.AttachmentImpl;

public class TaskAttachmentBuilder {

  private final AttachmentImpl attachment = new AttachmentImpl();

  public static TaskAttachmentBuilder newAttachment() {
    return new TaskAttachmentBuilder();
  }

  public TaskAttachmentBuilder created(Instant created) {
    attachment.setCreated(created);
    return this;
  }

  public TaskAttachmentBuilder modified(Instant modified) {
    attachment.setModified(modified);
    return this;
  }

  public TaskAttachmentBuilder received(Instant received) {
    attachment.setReceived(received);
    return this;
  }

  public TaskAttachmentBuilder classificationSummary(ClassificationSummary classificationSummary) {
    attachment.setClassificationSummary(classificationSummary);
    return this;
  }

  public TaskAttachmentBuilder objectReference(ObjectReference objectReference) {
    attachment.setObjectReference(objectReference);
    return this;
  }

  public TaskAttachmentBuilder channel(String channel) {
    attachment.setChannel(channel);
    return this;
  }

  public TaskAttachmentBuilder customAttributes(Map<String, String> customAttributes) {
    attachment.setCustomAttributes(customAttributes);
    return this;
  }

  public Attachment build() {
    AttachmentImpl a = attachment.copy();
    a.setTaskId(attachment.getTaskId());
    return a;
  }
}
