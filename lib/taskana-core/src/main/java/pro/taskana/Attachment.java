package pro.taskana;

import java.time.Instant;
import java.util.Map;

/**
 * Attachment-Interface to specify Attachment Attributes.
 */
public interface Attachment {

    /**
     * Returns the current id of the attachment.
     *
     * @return ID of attachment
     */
    String getId();

    /**
     * Returns the id of the associated task.
     *
     * @return taskId
     */
    String getTaskId();

    /**
     * Returns the time when the attachment was created.
     *
     * @return the created time as {@link Instant}
     */
    Instant getCreated();

    /**
     * Returns the time when the attachment was last modified.
     *
     * @return modified {@link Instant} of the attachment
     */
    Instant getModified();

    /**
     * Returns the classification summary of the attachment.
     *
     * @return the {@link ClassificationSummary} of this attachment
     */
    ClassificationSummary getClassificationSummary();

    /**
     * Set the classification summary for this attachment.
     *
     * @param classificationSummary
     *            the {@link ClassificationSummary} for this attachment
     */
    void setClassificationSummary(ClassificationSummary classificationSummary);

    /**
     * Returns the {@link ObjectReference primaryObjectReference} of the attachment.
     *
     * @return {@link ObjectReference primaryObjectReference} of the attachment
     **/
    ObjectReference getObjectReference();

    /**
     * Sets the {@link ObjectReference primaryObjectReference} of the attachment.
     *
     * @param objectReference
     *            the {@link ObjectReference primaryObjectReference} of the attachment
     */
    void setObjectReference(ObjectReference objectReference);

    /**
     * Returns the Channel on which the attachment was received.
     *
     * @return the Channel on which the attachment was received.
     **/
    String getChannel();

    /**
     * Sets the Channel on which the attachment was received.
     *
     * @param channel
     *            the channel on which the attachment was received
     */
    void setChannel(String channel);

    /**
     * Returns the time when this attachment was received.
     *
     * @return received time as exact {@link Instant}
     */
    Instant getReceived();

    /**
     * Sets the time when the attachment was received.
     *
     * @param received
     *            the time as {@link Instant} when the attachment was received
     **/
    void setReceived(Instant received);

    /**
     * Returns the custom attributes of this attachment.
     *
     * @return customAttributes as {@link Map}
     */
    Map<String, String> getCustomAttributes();

    /**
     * Sets the custom attribute Map of the attachment.
     *
     * @param customAttributes
     *            a {@link Map} that contains the custom attributes of the attachment as key, value pairs
     */
    void setCustomAttributes(Map<String, String> customAttributes);

    /**
     * Return a summary of the current Attachment.
     *
     * @return the AttachmentSummary object for the current attachment
     */
    AttachmentSummary asSummary();

}
