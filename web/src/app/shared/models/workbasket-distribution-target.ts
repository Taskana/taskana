import { WorkbasketType } from './workbasket-type';
import { WorkbasketSummary } from './workbasket-summary';

export interface WorkbasketDistributionTarget extends WorkbasketSummary {
  selected?: boolean;
}
