package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  /** the embedded classifications. */
  @Override
  @JsonProperty("classifications")
  public @NotNull Collection<ClassificationSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
