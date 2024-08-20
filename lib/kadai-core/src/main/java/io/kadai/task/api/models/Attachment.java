package io.kadai.task.api.models;

import io.kadai.classification.api.models.ClassificationSummary;
import java.time.Instant;
import java.util.Map;

/** Attachment-Interface to specify attributes of an Attachment. */
public interface Attachment extends AttachmentSummary {

  /**
   * Sets the {@linkplain ObjectReference primaryObjectReference} of the Attachment.
   *
   * @param objectReference the {@linkplain ObjectReference primaryObjectReference} of the
   *     Attachment
   */
  void setObjectReference(ObjectReference objectReference);

  /**
   * Set the {@linkplain ClassificationSummary classificationSummary} for this Attachment.
   *
   * @param classificationSummary the {@linkplain ClassificationSummary} for this Attachment
   */
  void setClassificationSummary(ClassificationSummary classificationSummary);

  /**
   * Sets the time when the Attachment was received.
   *
   * @param received the time when the Attachment was received as Instant
   */
  void setReceived(Instant received);

  /**
   * Sets the channel on which the Attachment was received.
   *
   * @param channel the channel on which the Attachment was received
   */
  void setChannel(String channel);

  /**
   * Returns the custom attributes of this Attachment.
   *
   * @return customAttributes as {@linkplain Map}
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets the custom attribute Map of the Attachment.
   *
   * @param customAttributes a {@linkplain Map} that contains the custom attributes of the
   *     Attachment as key, value pairs
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Returns a summary of the current Attachment.
   *
   * @return the {@linkplain AttachmentSummary} object for the current Attachment
   */
  AttachmentSummary asSummary();

  /**
   * Duplicates this Attachment without the id and taskId.
   *
   * @return a copy of this Attachment
   */
  Attachment copy();
}
