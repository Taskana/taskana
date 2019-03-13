package pro.taskana.rest.resource;

import org.springframework.hateoas.core.Relation;

import pro.taskana.WorkbasketSummary;

/**
 * Resource class for a distribution target based on {@link pro.taskana.WorkbasketSummary}.
 */
@Relation(collectionRelation = "distributionTargets")
public class DistributionTargetResource extends WorkbasketSummaryResource {

    DistributionTargetResource() {
    }

    DistributionTargetResource(WorkbasketSummary workbasketSummary) {
        super(workbasketSummary);
    }
}
