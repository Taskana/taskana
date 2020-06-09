import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export interface WorkbasketDistributionTargetsResource {
  distributionTargets: WorkbasketSummary[];
  _links: Links;
}
