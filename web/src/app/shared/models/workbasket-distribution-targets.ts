import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export interface WorkbasketDistributionTargets {
  distributionTargets: WorkbasketSummary[];
  _links: Links;
}
