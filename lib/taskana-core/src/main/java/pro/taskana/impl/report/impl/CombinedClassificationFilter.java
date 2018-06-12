package pro.taskana.impl.report.impl;

/**
 * The CombinedClassificationFilter is a pair of a classificationId for a task and a classificationId for the
 * corresponding attachment that is used to filter the {@link WorkbasketReport} by the classification of the attachment.
 * To filter by the classification of the task, the classificationId of the attachment should be null.
 */
public class CombinedClassificationFilter {

    private String taskClassificationId;
    private String attachmentClassificationId;

    public CombinedClassificationFilter(String taskClassificationId) {
        this.taskClassificationId = taskClassificationId;
    }

    public CombinedClassificationFilter(String taskClassificationId, String attachmentClassificationId) {
        this.taskClassificationId = taskClassificationId;
        this.attachmentClassificationId = attachmentClassificationId;
    }

    public String getTaskClassificationId() {
        return this.taskClassificationId;
    }

    public void setTaskClassificationId(String taskClassificationId) {
        this.taskClassificationId = taskClassificationId;
    }

    public String getAttachmentClassificationId() {
        return this.attachmentClassificationId;
    }

    public void setAttachmentClassificationId(String attachmentClassificationId) {
        this.attachmentClassificationId = attachmentClassificationId;
    }

    @Override
    public String toString() {
        return "(" + this.taskClassificationId + "," + this.attachmentClassificationId + ")";
    }

}
