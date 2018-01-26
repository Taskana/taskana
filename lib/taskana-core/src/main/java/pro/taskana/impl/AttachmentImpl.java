package pro.taskana.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.model.ObjectReference;

/**
 * Attachment entity.
 *
 * @author bbr
 */
public class AttachmentImpl implements Attachment {

    private String id;
    private String taskId;
    private Instant created;
    private Instant modified;
    private String classificationKey;
    private ClassificationSummary classificationSummary;
    private ObjectReference objectReference;
    private String channel;
    private Instant received;
    private Map<String, Object> customAttributes = Collections.emptyMap();

    AttachmentImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    @Override
    public ClassificationSummary getClassificationSummary() {
        return classificationSummary;
    }

    @Override
    public void setClassificationSummary(ClassificationSummary classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    // auxiliary method to enable MyBatis access to classificationSummary
    public ClassificationSummaryImpl getClassificationSummaryImpl() {
        return (ClassificationSummaryImpl) classificationSummary;
    }

    // auxiliary method to enable MyBatis access to classificationSummary
    public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    @Override
    public ObjectReference getObjectReference() {
        return objectReference;
    }

    @Override
    public void setObjectReference(ObjectReference objectReference) {
        this.objectReference = objectReference;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public Instant getReceived() {
        return received;
    }

    @Override
    public void setReceived(Instant received) {
        this.received = received;
    }

    @Override
    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    @Override
    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public AttachmentSummary asSummary() {
        AttachmentSummaryImpl summary = new AttachmentSummaryImpl();
        summary.setClassificationSummary(this.classificationSummary);
        summary.setCreated(this.created);
        summary.setId(this.id);
        summary.setModified(this.modified);
        summary.setReceived(this.received);
        summary.setTaskId(this.taskId);
        return summary;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AttachmentImpl [id=");
        builder.append(id);
        builder.append(", taskId=");
        builder.append(taskId);
        builder.append(", created=");
        builder.append(created.toString());
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", classificationKey=");
        builder.append(classificationKey);
        builder.append(", classificationSummary=");
        builder.append(classificationSummary);
        builder.append(", objectReference=");
        builder.append(objectReference);
        builder.append(", channel=");
        builder.append(channel);
        builder.append(", received=");
        builder.append(received);
        builder.append(", customAttributes=");
        builder.append(customAttributes);
        builder.append("]");
        return builder.toString();
    }
}
