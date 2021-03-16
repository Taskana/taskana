package pro.taskana.task.api.models;

import java.time.Instant;
import java.util.Map;

import pro.taskana.classification.api.models.ClassificationSummary;

/** Attachment-Interface to specify Attachment Attributes. */
public interface Attachment extends AttachmentSummary {

  /**
   * Sets the primary {@linkplain ObjectReference ObjectReference} of this Attachment}.
   *
   * @param objectReference the primary {@linkplain ObjectReference} of this Attachment}
   */
  void setObjectReference(ObjectReference objectReference);

  /**
   * Sets the {@linkplain ClassificationSummary} for this Attachment}.
   *
   * @param classificationSummary the {@linkplain ClassificationSummary} for this Attachment}
   */
  void setClassificationSummary(ClassificationSummary classificationSummary);

  /**
   * Sets the time when this Attachment} was received.
   *
   * @param received the time as {@linkplain Instant} when this Attachment} was received
   */
  void setReceived(Instant received);

  /**
   * Sets the {@linkplain java.nio.channels.Channel Channel} on which this Attachment} was received.
   *
   * @param channel the {@linkplain java.nio.channels.Channel Channel} on which this Attachment was
   *     received
   */
  void setChannel(String channel);

  /**
   * Returns the custom attributes of this Attachment}.
   *
   * @return customAttributes as Map
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets the custom attribute Map of this Attachment}.
   *
   * @param customAttributes a Map that contains the custom attributes of this Attachment} as key,
   *     value pairs
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Returns a {@linkplain AttachmentSummary} of this Attachment}.
   *
   * @return the {@linkplain AttachmentSummary} object for this Attachment}
   */
  AttachmentSummary asSummary();

  /**
   * Duplicates this Attachment} without the id and taskId.
   *
   * @return a copy of this Attachment}
   */
  Attachment copy();
}
