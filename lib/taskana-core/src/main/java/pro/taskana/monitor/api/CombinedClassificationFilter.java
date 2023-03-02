package pro.taskana.monitor.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;

/**
 * The CombinedClassificationFilter is a pair of a classificationId for a {@linkplain Task} and a
 * classificationId for the corresponding {@linkplain Attachment}. Such pair can only be created for
 * tasks that have no more than one {@linkplain Attachment}. The pair is used to filter the
 * {@linkplain WorkbasketReport} by the {@linkplain Classification} of the {@linkplain Attachment}.
 * To filter by the {@linkplain Classification} of the {@linkplain Task}, the
 * attachmentClassificationId of the CombinedClassificationFilter should be NULL.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CombinedClassificationFilter {

  private String taskClassificationId;
  private String attachmentClassificationId;

  public CombinedClassificationFilter(String taskClassificationId) {
    this.taskClassificationId = taskClassificationId;
  }
}
