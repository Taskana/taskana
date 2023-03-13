package pro.taskana.task.api.models;

import java.time.Instant;
import pro.taskana.classification.api.models.ClassificationSummary;

/**
 * Interface for AttachmentSummaries. This is a specific short model-object which only contains the
 * most important information.
 */
public interface AttachmentSummary {

  /**
   * Returns the id of the {@linkplain Attachment}.
   *
   * @return attachmentId
   */
  String getId();

  /**
   * Returns the id of the associated {@linkplain Task}.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the time when the {@linkplain Attachment} was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the {@linkplain Attachment} was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Returns the {@linkplain ObjectReference primaryObjectReference} of the {@linkplain Attachment}.
   *
   * @return {@linkplain ObjectReference primaryObjectReference} of the {@linkplain Attachment}
   */
  ObjectReference getObjectReference();

  /**
   * Returns the channel on which the {@linkplain Attachment} was received.
   *
   * @return the channel
   */
  String getChannel();

  /**
   * Returns the {@linkplain ClassificationSummary classificationSummary} of the {@linkplain
   * Attachment}.
   *
   * @return {@linkplain ClassificationSummary classificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Returns the time when the {@linkplain Attachment} was received.
   *
   * @return the received Instant
   */
  Instant getReceived();

  /**
   * Duplicates this AttachmentSummary without the id and taskId.
   *
   * @return a copy of this AttachmentSummary
   */
  AttachmentSummary copy();
}
