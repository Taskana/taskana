package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;

@Schema(
    description =
        "this class represents a workbasket including its distro targets and " + "authorisations.")
public class WorkbasketDefinitionRepresentationModel
    extends RepresentationModel<WorkbasketDefinitionRepresentationModel> {

  @Schema(name = "workbasket", description = "The workbasket which is represented.")
  @JsonIgnoreProperties("_links")
  private WorkbasketRepresentationModel workbasket;

  @Schema(name = "authorizations", description = "The workbasket authorizations.")
  private Collection<WorkbasketAccessItemRepresentationModel> authorizations = new ArrayList<>();

  @Schema(
      name = "distributionTargets",
      description = "The distribution targets for this workbasket.")
  private Set<String> distributionTargets = new HashSet<>();

  public Set<String> getDistributionTargets() {
    return distributionTargets;
  }

  public void setDistributionTargets(Set<String> distributionTargets) {
    this.distributionTargets = distributionTargets;
  }

  public Collection<WorkbasketAccessItemRepresentationModel> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(
      Collection<WorkbasketAccessItemRepresentationModel> authorizations) {
    this.authorizations = authorizations;
  }

  public WorkbasketRepresentationModel getWorkbasket() {
    return workbasket;
  }

  public void setWorkbasket(WorkbasketRepresentationModel workbasket) {
    this.workbasket = workbasket;
  }
}
