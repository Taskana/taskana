package pro.taskana.classification.rest.models;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.api.models.ClassificationSummary;

/** EntityModel class for {@link ClassificationSummary}. */
@Getter
@Setter
public class ClassificationSummaryRepresentationModel
    extends RepresentationModel<ClassificationSummaryRepresentationModel> {

  /** Unique Id. */
  @NotNull protected String classificationId;
  /**
   * The key of the Classification. This is typically an externally known code or abbreviation of
   * the Classification.
   */
  @NotNull protected String key;
  /**
   * The logical name of the entry point. This is needed by the task list application to determine
   * the redirect to work on a task of this Classification.
   */
  protected String applicationEntryPoint;
  /**
   * The category of the classification. Categories can be configured in the file
   * 'taskana.properties'.
   */
  @NotNull protected String category;
  /** The domain for which this classification is specified. */
  protected String domain;
  /** The name of the classification. */
  @NotNull protected String name;
  /** The Id of the parent classification. Empty string ("") if this is a root classification. */
  protected String parentId;
  /** The key of the parent classification. Empty string ("") if this is a root classification. */
  protected String parentKey;
  /** The priority of the classification. */
  @NotNull protected int priority;
  /**
   * The service level of the classification.
   *
   * <p>This is stated according to ISO 8601.
   */
  @NotNull protected String serviceLevel;
  /** The type of classification. Types can be configured in the file 'taskana.properties'. */
  protected String type;
  /** A custom property with name "1". */
  protected String custom1;
  /** A custom property with name "2". */
  protected String custom2;
  /** A custom property with name "3". */
  protected String custom3;
  /** A custom property with name "4". */
  protected String custom4;
  /** A custom property with name "5". */
  protected String custom5;
  /** A custom property with name "6". */
  protected String custom6;
  /** A custom property with name "7". */
  protected String custom7;
  /** A custom property with name "8". */
  protected String custom8;
}
