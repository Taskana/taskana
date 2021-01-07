package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class WorkbasketAccessItemCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @ConstructorProperties("accessItems")
  public WorkbasketAccessItemCollectionRepresentationModel(
      Collection<WorkbasketAccessItemRepresentationModel> content) {
    super(content);
  }

  /** the embedded access items. */
  @JsonProperty("accessItems")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
