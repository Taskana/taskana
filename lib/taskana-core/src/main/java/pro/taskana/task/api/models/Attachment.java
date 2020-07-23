package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.Map;

import pro.taskana.classification.api.models.ClassificationSummary;

/** Attachment-Interface to specify Attachment Attributes. */
public interface Attachment extends AttachmentSummary {

  /**
   * Sets the {@link ObjectReference primaryObjectReference} of the attachment.
   *
   * @param objectReference the {@link ObjectReference primaryObjectReference} of the attachment
   */
  void setObjectReference(ObjectReference objectReference);

  /**
   * Set the classification summary for this attachment.
   *
   * @param classificationSummary the {@link ClassificationSummary} for this attachment
   */
  void setClassificationSummary(ClassificationSummary classificationSummary);

  /**
   * Sets the time when the attachment was received.
   *
   * @param received the time as {@link Instant} when the attachment was received
   */
  void setReceived(Instant received);

  /**
   * Sets the Channel on which the attachment was received.
   *
   * @param channel the channel on which the attachment was received
   */
  void setChannel(String channel);

  /**
   * Returns the custom attributes of this attachment.
   *
   * @return customAttributes as {@link Map}
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets the custom attribute Map of the attachment.
   *
   * @param customAttributes a {@link Map} that contains the custom attributes of the attachment as
   *     key, value pairs
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Return a summary of the current Attachment.
   *
   * @return the AttachmentSummary object for the current attachment
   */
  AttachmentSummary asSummary();

  /**
   * Duplicates this Attachment without the id and taskId.
   *
   * @return a copy of this Attachment
   */
  Attachment copy();
}
