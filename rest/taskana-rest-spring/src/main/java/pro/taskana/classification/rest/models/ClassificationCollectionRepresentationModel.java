package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class ClassificationCollectionRepresentationModel
    extends CollectionRepresentationModel<ClassificationRepresentationModel> {

  @ConstructorProperties("classifications")
  public ClassificationCollectionRepresentationModel(
      Collection<ClassificationRepresentationModel> content) {
    super(content);
  }

  /** the embedded classifications. */
  @Override
  @JsonProperty("classifications")
  public Collection<ClassificationRepresentationModel> getContent() {
    return super.getContent();
  }
}
