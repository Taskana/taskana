package pro.taskana.classification.rest.models;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

import pro.taskana.classification.api.models.Classification;

/** EntityModel class for {@link Classification}. */
@Getter
@Setter
public class ClassificationRepresentationModel extends ClassificationSummaryRepresentationModel {

  /** True, if this classification to objects in this domain. */
  private Boolean isValidInDomain;
  /**
   * The creation timestamp of the classification in the system.
   *
   * <p>The format is ISO-8601.
   */
  private Instant created;
  /**
   * The timestamp of the last modification.
   *
   * <p>The format is ISO-8601.
   */
  private Instant modified;
  /** The description of the classification. */
  private String description;
}
