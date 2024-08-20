package io.kadai.classification.rest.assembler;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.classification.rest.models.ClassificationDefinitionRepresentationModel;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class ClassificationDefinitionCollectionRepresentationModel
    extends CollectionRepresentationModel<ClassificationDefinitionRepresentationModel> {

  @ConstructorProperties("classifications")
  public ClassificationDefinitionCollectionRepresentationModel(
      Collection<ClassificationDefinitionRepresentationModel> content) {
    super(content);
  }

  /** the embedded classification definitions. */
  @JsonProperty("classifications")
  @Override
  public Collection<ClassificationDefinitionRepresentationModel> getContent() {
    return super.getContent();
  }
}
