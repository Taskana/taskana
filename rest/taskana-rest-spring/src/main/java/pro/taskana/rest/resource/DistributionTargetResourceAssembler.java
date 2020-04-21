package pro.taskana.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * Transforms WorkbasketSummary to its resource counterpart DistributionTargerResource and vice
 * versa.
 */
@Component
public class DistributionTargetResourceAssembler
    extends RepresentationModelAssemblerSupport<WorkbasketSummary, DistributionTargetResource> {

  public DistributionTargetResourceAssembler() {
    super(WorkbasketController.class, DistributionTargetResource.class);
  }

  public DistributionTargetResource toModel(WorkbasketSummary summary) {
    return new DistributionTargetResource(summary);
  }

  public DistributionTargetListResource toCollectionModel(
      String workbasketId, List<WorkbasketSummary> distributionTargets)
      throws WorkbasketNotFoundException, NotAuthorizedException {

    DistributionTargetListResource distributionTargetListResource =
        new DistributionTargetListResource(toCollectionModel(distributionTargets).getContent());
    distributionTargetListResource.add(
        linkTo(methodOn(WorkbasketController.class).getDistributionTargets(workbasketId))
            .withSelfRel());
    distributionTargetListResource.add(
        linkTo(methodOn(WorkbasketController.class).getWorkbasket(workbasketId))
            .withRel("workbasket"));

    return distributionTargetListResource;
  }
}
