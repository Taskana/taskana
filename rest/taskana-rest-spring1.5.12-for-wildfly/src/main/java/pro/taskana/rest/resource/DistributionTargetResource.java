package pro.taskana.rest.resource;

import org.springframework.hateoas.core.Relation;

/**
 * Resource class for a distribution target based on {@link pro.taskana.WorkbasketSummary}.
 */
@Relation(collectionRelation = "distributionTargets")
public class DistributionTargetResource extends WorkbasketSummaryResource {

}
