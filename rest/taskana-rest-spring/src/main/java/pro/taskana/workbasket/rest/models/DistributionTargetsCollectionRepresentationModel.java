package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class DistributionTargetsCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketSummaryRepresentationModel> {

  @ConstructorProperties({"distributionTargets"})
  public DistributionTargetsCollectionRepresentationModel(
      Collection<WorkbasketSummaryRepresentationModel> content) {
    super(content);
  }

  /** the embedded distribution targets. */
  @JsonProperty("distributionTargets")
  @Override
  public Collection<WorkbasketSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
