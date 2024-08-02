package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.util.Collection;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public class ClassificationSummaryPagedRepresentationModel
    extends PagedRepresentationModel<ClassificationSummaryRepresentationModel> {

  @ConstructorProperties({"classifications", "page"})
  public ClassificationSummaryPagedRepresentationModel(
      Collection<ClassificationSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  @Schema(name = "classifications", description = "the embedded classifications.")
  @Override
  @JsonProperty("classifications")
  public @NotNull Collection<ClassificationSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
