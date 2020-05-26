package pro.taskana.workbasket.rest.models;

import java.util.List;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/** this class represents a workbasket including its distro targets and authorisations. */
public class WorkbasketDefinitionRepresentationModel
    extends RepresentationModel<WorkbasketDefinitionRepresentationModel> {

  private Set<String> distributionTargets;
  private List<WorkbasketAccessItemImpl> authorizations;
  private WorkbasketRepresentationModelWithoutLinks workbasket;

  public Set<String> getDistributionTargets() {
    return distributionTargets;
  }

  public void setDistributionTargets(Set<String> distributionTargets) {
    this.distributionTargets = distributionTargets;
  }

  public List<WorkbasketAccessItemImpl> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(List<WorkbasketAccessItemImpl> authorizations) {
    this.authorizations = authorizations;
  }

  public WorkbasketRepresentationModelWithoutLinks getWorkbasket() {
    return workbasket;
  }

  public void setWorkbasket(WorkbasketRepresentationModelWithoutLinks workbasket) {
    this.workbasket = workbasket;
  }
}
