package pro.taskana;

import java.sql.Timestamp;

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
     * @return the created timestamp
     */
    Timestamp getCreated();

    /**
     * Gets the time when the attachment was last modified.
     *
     * @return the last modified timestamp
     */
    Timestamp getModified();

    /**
     * Gets the classificationSummary of the attachment.
     *
     * @return the classification summary
     */
    ClassificationSummary getClassificationSummary();

    /**
     * Gets the time when the attachment was received.
     *
     * @return received timestamp
     */
    Timestamp getReceived();

}
