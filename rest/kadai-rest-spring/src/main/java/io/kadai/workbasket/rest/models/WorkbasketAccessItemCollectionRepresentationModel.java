package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketAccessItemCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @ConstructorProperties("accessItems")
  public WorkbasketAccessItemCollectionRepresentationModel(
      Collection<WorkbasketAccessItemRepresentationModel> content) {
    super(content);
  }

  @JsonProperty("accessItems")
  @Schema(name = "accessItems", description = "the embedded access items.")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
