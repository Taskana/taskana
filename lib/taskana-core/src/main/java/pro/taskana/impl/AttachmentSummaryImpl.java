package pro.taskana.impl;

import java.time.Instant;

import pro.taskana.AttachmentSummary;
import pro.taskana.ClassificationSummary;
import pro.taskana.ObjectReference;

/**
 * The most important fields of the Attachment entity.
 */
public class AttachmentSummaryImpl implements AttachmentSummary {

    private String id;
    private String taskId;
    private Instant created;
    private Instant modified;
    private ClassificationSummary classificationSummary;
    private ObjectReference objectReference;
    private String channel;
    private Instant received;

    AttachmentSummaryImpl() {

    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getTaskId()
     */
    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getCreated()
     */
    @Override
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getModified()
     */
    @Override
    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getClassification()
     */
    @Override
    public ClassificationSummary getClassificationSummary() {
        return classificationSummary;
    }

    public void setClassificationSummary(ClassificationSummary classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.AttachmentSummary#getObjectReference()
     */
    @Override
    public ObjectReference getObjectReference() {
        return objectReference;
    }

    public void setObjectReference(ObjectReference objectReference) {
        this.objectReference = objectReference;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.AttachmentSummary#getChannel()
     */
    @Override
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    // auxiliary method to enable MyBatis access to classificationSummary
    public ClassificationSummaryImpl getClassificationSummaryImpl() {
        return (ClassificationSummaryImpl) classificationSummary;
    }

    // auxiliary method to enable MyBatis access to classificationSummary
    public void setClassificationSummaryImpl(ClassificationSummaryImpl classificationSummary) {
        this.classificationSummary = classificationSummary;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.AttachmentSummary#getReceived()
     */
    @Override
    public Instant getReceived() {
        return received;
    }

    public void setReceived(Instant received) {
        this.received = received;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((classificationSummary == null) ? 0 : classificationSummary.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
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
        if (!(obj instanceof AttachmentSummaryImpl)) {
            return false;
        }
        AttachmentSummaryImpl other = (AttachmentSummaryImpl) obj;
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
        if (objectReference == null) {
            if (other.objectReference != null) {
                return false;
            }
        } else if (!objectReference.equals(other.objectReference)) {
            return false;
        }
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AttachmentSummaryImpl [id=");
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
        builder.append("]");
        return builder.toString();
    }

}
