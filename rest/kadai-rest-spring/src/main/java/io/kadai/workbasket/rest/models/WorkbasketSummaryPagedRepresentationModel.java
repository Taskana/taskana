package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketSummaryPagedRepresentationModel
    extends PagedRepresentationModel<WorkbasketSummaryRepresentationModel> {

  @ConstructorProperties({"workbaskets", "page"})
  public WorkbasketSummaryPagedRepresentationModel(
      Collection<WorkbasketSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  @Schema(name = "workbaskets", description = "the embedded workbaskets.")
  @JsonProperty("workbaskets")
  @Override
  public Collection<WorkbasketSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
