import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export class WorkbasketDistributionTargetsResource {
  constructor(
    public distributionTargets: Array<WorkbasketSummary> = [],
    public _links: Links = null
  ) {
  }
}
