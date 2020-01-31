package pro.taskana.rest.resource;

import org.springframework.hateoas.core.Relation;

import pro.taskana.workbasket.api.WorkbasketSummary;

/** Resource class for a distribution target based on {@link WorkbasketSummary}. */
@Relation(collectionRelation = "distributionTargets")
public class DistributionTargetResource extends WorkbasketSummaryResource {

  DistributionTargetResource() {}

  DistributionTargetResource(WorkbasketSummary workbasketSummary) {
    super(workbasketSummary);
  }
}
