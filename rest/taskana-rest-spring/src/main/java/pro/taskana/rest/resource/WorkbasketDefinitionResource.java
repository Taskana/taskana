package pro.taskana.rest.resource;

import java.util.List;
import java.util.Set;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.workbasket.internal.WorkbasketAccessItemImpl;

/** this class represents a workbasket including its distro targets and authorisations. */
public class WorkbasketDefinitionResource {

  private Set<String> distributionTargets;
  private List<WorkbasketAccessItemImpl> authorizations;
  private WorkbasketResourceWithoutLinks workbasket;

  public WorkbasketDefinitionResource() {
    // necessary for de-serializing
  }

  public WorkbasketDefinitionResource(
      WorkbasketResourceWithoutLinks workbasket,
      Set<String> distributionTargets,
      List<WorkbasketAccessItemImpl> authorizations) {
    super();
    this.workbasket = workbasket;
    this.distributionTargets = distributionTargets;
    this.authorizations = authorizations;
  }

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

  public WorkbasketResourceWithoutLinks getWorkbasket() {
    return workbasket;
  }

  public void setWorkbasket(WorkbasketResourceWithoutLinks workbasket) {
    this.workbasket = workbasket;
  }

  @Override
  public String toString() {
    return "WorkbasketDefinitionResource ["
        + "distributionTargets= "
        + LoggerUtils.setToString(this.distributionTargets)
        + "authorizations= "
        + LoggerUtils.listToString(this.authorizations)
        + "workbasket= "
        + this.workbasket
        + "]";
  }
}
