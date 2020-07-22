import { WorkbasketAccessItems } from './workbasket-access-items';
import { Workbasket } from './workbasket';

export interface WorkbasketDefinition {
  distributionTargets: string[];
  workbasketAccessItems: WorkbasketAccessItems[];
  workbasket: Workbasket;
}
