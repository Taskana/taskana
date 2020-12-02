package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public class WorkbasketSummaryPagedRepresentationModel
    extends PagedRepresentationModel<WorkbasketSummaryRepresentationModel> {

  @ConstructorProperties({"workbaskets", "page"})
  public WorkbasketSummaryPagedRepresentationModel(
      Collection<WorkbasketSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded workbaskets. */
  @JsonProperty("workbaskets")
  @Override
  public Collection<WorkbasketSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
