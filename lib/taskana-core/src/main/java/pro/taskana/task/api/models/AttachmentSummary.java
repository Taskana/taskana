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
   * @return the created {@linkplain Instant}
   */
  Instant getCreated();

  /**
   * Returns the time when the {@linkplain Attachment} was last modified.
   *
   * @return the last modified {@linkplain Instant}
   */
  Instant getModified();

  /**
   * Returns the primary {@linkplain ObjectReference ObjectReference} of the {@linkplain
   * Attachment}.
   *
   * @return primary {@linkplain ObjectReference} of the {@linkplain Attachment}
   */
  ObjectReference getObjectReference();

  /**
   * Returns the {@linkplain java.nio.channels.Channel Channel} on which the {@linkplain Attachment}
   * was received.
   *
   * @return the {@linkplain java.nio.channels.Channel Channel}
   */
  String getChannel();

  /**
   * Returns the {@linkplain ClassificationSummary} of the {@linkplain Attachment}.
   *
   * @return the {@linkplain ClassificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Returns the time when the {@linkplain Attachment} was received.
   *
   * @return received {@linkplain Instant}
   */
  Instant getReceived();

  /**
   * Duplicates this {@linkplain AttachmentSummary} without the id and taskId.
   *
   * @return a copy of this {@linkplain AttachmentSummary}
   */
  AttachmentSummary copy();
}
