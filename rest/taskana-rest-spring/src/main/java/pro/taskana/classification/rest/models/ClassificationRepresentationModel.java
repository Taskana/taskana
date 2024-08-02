package pro.taskana.classification.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import pro.taskana.classification.api.models.Classification;

/** EntityModel class for {@link Classification}. */
@Schema(description = "EntityModel class for Classification")
public class ClassificationRepresentationModel extends ClassificationSummaryRepresentationModel {

  @Schema(
      name = "isValidInDomain",
      description = "True, if this classification to objects in this domain.")
  private Boolean isValidInDomain;
  @Schema(
      name = "created",
      description =
          "The creation timestamp of the classification in the system.<p>The format is ISO-8601.")
  private Instant created;
  @Schema(
          name = "modified",
          description = "The timestamp of the last modification.<p>The format is ISO-8601."
  )
  private Instant modified;
  @Schema(name = "description", description = "The description of the classification.")
  private String description;

  public Boolean getIsValidInDomain() {
    return isValidInDomain;
  }

  public void setIsValidInDomain(Boolean isValidInDomain) {
    this.isValidInDomain = isValidInDomain;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
