package pro.taskana.rest.resource;

import pro.taskana.classification.api.models.Classification;

/**
 * EntityModel class for {@link Classification}.
 */
public class ClassificationRepresentationModel
    extends ClassificationSummaryRepresentationModel {

  private Boolean isValidInDomain;
  private String created; // ISO-8601
  private String modified; // ISO-8601
  private String description;

  public ClassificationRepresentationModel() {
  }

  public ClassificationRepresentationModel(Classification classification) {
    super(classification);
    this.isValidInDomain = classification.getIsValidInDomain();
    this.created =
        classification.getCreated() != null ? classification.getCreated().toString() : null;
    this.modified =
        classification.getModified() != null ? classification.getModified().toString() : null;
    this.description = classification.getDescription();
  }

  public Boolean getIsValidInDomain() {
    return isValidInDomain;
  }

  public void setIsValidInDomain(Boolean validInDomain) {
    isValidInDomain = validInDomain;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "ClassificationResource [classificationId="
               + classificationId
               + ", key="
               + key
               + ", parentId="
               + parentId
               + ", parentKey="
               + parentKey
               + ", category="
               + category
               + ", type="
               + type
               + ", domain="
               + domain
               + ", isValidInDomain="
               + isValidInDomain
               + ", created="
               + created
               + ", modified="
               + modified
               + ", name="
               + name
               + ", description="
               + description
               + ", priority="
               + priority
               + ", serviceLevel="
               + serviceLevel
               + ", applicationEntryPoint="
               + applicationEntryPoint
               + ", custom1="
               + custom1
               + ", custom2="
               + custom2
               + ", custom3="
               + custom3
               + ", custom4="
               + custom4
               + ", custom5="
               + custom5
               + ", custom6="
               + custom6
               + ", custom7="
               + custom7
               + ", custom8="
               + custom8
               + "]";
  }
}
