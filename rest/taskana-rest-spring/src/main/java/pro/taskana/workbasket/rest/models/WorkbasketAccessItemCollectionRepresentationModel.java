package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(name = "accessItems", description = "the embedded access items.")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
