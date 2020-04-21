package pro.taskana.rest.resource;

import org.springframework.hateoas.server.core.Relation;

import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** EntityModel class for a distribution target based on {@link WorkbasketSummary}. */
@Relation(collectionRelation = "distributionTargets")
public class DistributionTargetResource extends WorkbasketSummaryResource {

  DistributionTargetResource() {}

  DistributionTargetResource(WorkbasketSummary workbasketSummary) {
    super(workbasketSummary);
  }
}
