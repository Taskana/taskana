package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;

/** this class represents a workbasket including its distro targets and authorisations. */
public class WorkbasketDefinitionRepresentationModel
    extends RepresentationModel<WorkbasketDefinitionRepresentationModel> {

  @JsonIgnoreProperties("_links")
  private WorkbasketRepresentationModel workbasket;

  private Collection<WorkbasketAccessItemRepresentationModel> authorizations;
  private Set<String> distributionTargets;

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
