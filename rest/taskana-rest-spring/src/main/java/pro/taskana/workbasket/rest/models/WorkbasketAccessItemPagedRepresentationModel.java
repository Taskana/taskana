package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public class WorkbasketAccessItemPagedRepresentationModel
    extends PagedRepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @ConstructorProperties({"accessItems", "page"})
  public WorkbasketAccessItemPagedRepresentationModel(
      Collection<WorkbasketAccessItemRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  @Schema(name = "accessItems", description = "the embedded access items.")
  @JsonProperty("accessItems")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
