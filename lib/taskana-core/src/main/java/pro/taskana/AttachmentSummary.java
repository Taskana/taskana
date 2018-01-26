package pro.taskana;

import java.time.Instant;

/**
 * Interface for AttachmentSummaries. This is a specific short model-object which only contains the most important
 * information.
 */
public interface AttachmentSummary {

    /**
     * Gets the id of the attachment.
     *
     * @return attachmentId
     */
    String getId();

    /**
     * Gets the id of the associated task.
     *
     * @return taskId
     */
    String getTaskId();

    /**
     * Gets the time when the attachment was created.
     *
     * @return the created Instant
     */
    Instant getCreated();

    /**
     * Gets the time when the attachment was last modified.
     *
     * @return the last modified Instant
     */
    Instant getModified();

    /**
     * Gets the classificationSummary of the attachment.
     *
     * @return the classification summary
     */
    ClassificationSummary getClassificationSummary();

    /**
     * Gets the time when the attachment was received.
     *
     * @return received Instant
     */
    Instant getReceived();
}
