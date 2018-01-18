package pro.taskana.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

import pro.taskana.Attachment;
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
    private Timestamp created;
    private Timestamp modified;
    private String classificationKey;
    private ClassificationSummary classificationSummary;
    private ObjectReference objectReference;
    private String channel;
    private Timestamp received;
    private Map<String, Object> customAttributes = Collections.emptyMap();;

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

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public String getClassificationKey() {
        return classificationKey;
    }

    @Override
    public void setClassificationKey(String classificationKey) {
        this.classificationKey = classificationKey;
    }

    @Override
    public ClassificationSummary getClassificationSummary() {
        return classificationSummary;
    }

    public void setClassificationSummary(ClassificationSummary classificationSummary) {
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
    public Timestamp getReceived() {
        return received;
    }

    @Override
    public void setReceived(Timestamp received) {
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
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attachment [id=");
        builder.append(id);
        builder.append(", taskId=");
        builder.append(taskId);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", classificationKey=");
        builder.append(classificationKey);
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
