package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketDefinitionCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketDefinitionRepresentationModel> {

  @ConstructorProperties("workbasketDefinitions")
  public WorkbasketDefinitionCollectionRepresentationModel(
      Collection<WorkbasketDefinitionRepresentationModel> content) {
    super(content);
  }

  @Schema(name = "workbasketDefinitions", description = "the embedded workbasket definitions.")
  @JsonProperty("workbasketDefinitions")
  @Override
  public Collection<WorkbasketDefinitionRepresentationModel> getContent() {
    return super.getContent();
  }
}
