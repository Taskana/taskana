package pro.taskana.workbasket.rest.assembler;

import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.DISTRIBUTION_TARGETS;

import org.springframework.stereotype.Component;

import pro.taskana.common.rest.models.TaskanaPagedModelKeys;

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
