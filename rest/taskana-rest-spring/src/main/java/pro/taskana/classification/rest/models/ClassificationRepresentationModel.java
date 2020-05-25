package pro.taskana.classification.rest.models;

import java.time.Instant;

import pro.taskana.classification.api.models.Classification;

/** EntityModel class for {@link Classification}. */
public class ClassificationRepresentationModel extends ClassificationSummaryRepresentationModel {

  private Boolean isValidInDomain;

  private Instant created; // ISO-8601
  private Instant modified; // ISO-8601
  private String description;

  public ClassificationRepresentationModel() {}

  public ClassificationRepresentationModel(Classification classification) {
    super(classification);
    this.isValidInDomain = classification.getIsValidInDomain();
    this.created = classification.getCreated();
    this.modified = classification.getModified();
    this.description = classification.getDescription();
  }

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
