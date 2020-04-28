package pro.taskana.rest.resource;

import static pro.taskana.rest.resource.TaskanaPagedModelKeys.DISTRIBUTION_TARGETS;

import org.springframework.stereotype.Component;

/**
 * Transforms WorkbasketSummary to its resource counterpart DistributionTargerResource and vice
 * versa.
 */
@Component
public class DistributionTargetRepresentationModelAssembler
    extends WorkbasketSummaryRepresentationModelAssembler {

  @Override
  protected TaskanaPagedModelKeys getKey() {
    return DISTRIBUTION_TARGETS;
  }
}
