package pro.taskana.monitor.api;

import pro.taskana.monitor.api.reports.WorkbasketReport;

/**
 * The CombinedClassificationFilter is a pair of a classificationId for a {@linkplain
 * pro.taskana.task.api.models.Task Task} and a classificationId for the corresponding {@linkplain
 * pro.taskana.task.api.models.Attachment Attachment} that is used to filter the {@linkplain
 * WorkbasketReport} by the {@linkplain pro.taskana.classification.api.models.Classification
 * Classification} of the {@linkplain pro.taskana.task.api.models.Attachment Attachment}.
 *
 * <p>To filter by the {@linkplain pro.taskana.classification.api.models.Classification
 * Classification} of the {@linkplain pro.taskana.task.api.models.Task Tasks}, the classificationId
 * of the {@linkplain pro.taskana.task.api.models.Attachment Attachment} should be null.
 */
public class CombinedClassificationFilter {

  private String taskClassificationId;
  private String attachmentClassificationId;

  public CombinedClassificationFilter(String taskClassificationId) {
    this.taskClassificationId = taskClassificationId;
  }

  public CombinedClassificationFilter(
      String taskClassificationId, String attachmentClassificationId) {
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
    return "CombinedClassificationFilter [taskClassificationId="
        + taskClassificationId
        + ", attachmentClassificationId="
        + attachmentClassificationId
        + "]";
  }
}
