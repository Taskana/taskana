package io.kadai.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class ClassificationCollectionRepresentationModel
    extends CollectionRepresentationModel<ClassificationRepresentationModel> {

  @ConstructorProperties("classifications")
  public ClassificationCollectionRepresentationModel(
      Collection<ClassificationRepresentationModel> content) {
    super(content);
  }

  @Override
  @JsonProperty("classifications")
  public Collection<ClassificationRepresentationModel> getContent() {
    return super.getContent();
  }
}
