package pro.taskana.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;

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
    private ClassificationSummary classificationSummary;
    private ObjectReference objectReference;
    private String channel;
    private Instant received;
    private Map<String, String> customAttributes = new HashMap<String, String>();

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
    public Map<String, String> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new HashMap<>();
        }
        return customAttributes;
    }

    @Override
    public void setCustomAttributes(Map<String, String> customAttributes) {
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
        summary.setChannel(this.channel);
        summary.setObjectReference(this.objectReference);
        return summary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((classificationSummary == null) ? 0 : classificationSummary.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((customAttributes == null) ? 0 : customAttributes.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((objectReference == null) ? 0 : objectReference.hashCode());
        result = prime * result + ((received == null) ? 0 : received.hashCode());
        result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AttachmentImpl other = (AttachmentImpl) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        if (classificationSummary == null) {
            if (other.classificationSummary != null) {
                return false;
            }
        } else if (!classificationSummary.equals(other.classificationSummary)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (customAttributes == null) {
            if (other.customAttributes != null) {
                return false;
            }
        } else if (!customAttributes.equals(other.customAttributes)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (modified == null) {
            if (other.modified != null) {
                return false;
            }
        } else if (!modified.equals(other.modified)) {
            return false;
        }
        if (objectReference == null) {
            if (other.objectReference != null) {
                return false;
            }
        } else if (!objectReference.equals(other.objectReference)) {
            return false;
        }
        if (received == null) {
            if (other.received != null) {
                return false;
            }
        } else if (!received.equals(other.received)) {
            return false;
        }
        if (taskId == null) {
            if (other.taskId != null) {
                return false;
            }
        } else if (!taskId.equals(other.taskId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AttachmentImpl [id=");
        builder.append(id);
        builder.append(", taskId=");
        builder.append(taskId);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
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
