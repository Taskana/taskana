package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;


public class ClassificationDefinitionRepresentationModel extends
    RepresentationModel<ClassificationDefinitionRepresentationModel> {

  @JsonIgnoreProperties("_links")
  @JsonUnwrapped
  private final ClassificationRepresentationModel classification;

  @JsonCreator
  public ClassificationDefinitionRepresentationModel(
      ClassificationRepresentationModel classification) {
    this.classification = classification;
  }

  public ClassificationRepresentationModel getClassification() {
    return classification;
  }
}
