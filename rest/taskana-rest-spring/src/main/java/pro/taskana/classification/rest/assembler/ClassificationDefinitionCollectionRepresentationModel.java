package pro.taskana.classification.rest.assembler;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.classification.rest.models.ClassificationDefinitionRepresentationModel;
import pro.taskana.common.rest.models.CollectionRepresentationModel;

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
