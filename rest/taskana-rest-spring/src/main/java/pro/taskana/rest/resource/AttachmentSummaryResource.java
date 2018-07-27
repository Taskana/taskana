package pro.taskana.rest.resource;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.ObjectReference;

/**
 * Resource class for {@link pro.taskana.AttachmentSummary}.
 */
public class AttachmentSummaryResource extends ResourceSupport {

    private String attachmentId;
    private String taskId;
    private String created;
    private String  modified;
    private ClassificationSummaryResource classificationSummaryResource;
    private ObjectReference objectReference;
    private String channel;
    private String received;

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public ClassificationSummaryResource getClassificationSummary() {
        return classificationSummaryResource;
    }

    public void setClassificationSummary(ClassificationSummaryResource classificationSummaryResource) {
        this.classificationSummaryResource = classificationSummaryResource;
    }

    public ObjectReference getObjectReference() {
        return objectReference;
    }

    public void setObjectReference(ObjectReference objectReference) {
        this.objectReference = objectReference;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }
}
