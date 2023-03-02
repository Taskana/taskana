package pro.taskana.task.rest.models;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.task.api.models.AttachmentSummary;

/** EntityModel class for {@link AttachmentSummary}. */
@Getter
@Setter
public class AttachmentSummaryRepresentationModel
    extends RepresentationModel<AttachmentSummaryRepresentationModel> {

  /** Unique Id. */
  protected String attachmentId;
  /** the referenced task id. */
  protected String taskId;
  /** The creation timestamp in the system. */
  protected Instant created;
  /** The timestamp of the last modification. */
  protected Instant modified;
  /** The timestamp of the entry date. */
  protected Instant received;
  /** The classification of this attachment. */
  protected ClassificationSummaryRepresentationModel classificationSummary;
  /** The Objects primary ObjectReference. */
  protected ObjectReferenceRepresentationModel objectReference;
  /** Determines on which channel this attachment was received. */
  protected String channel;
}
