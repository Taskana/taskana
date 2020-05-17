import { WorkbasketAccessItems } from './workbasket-access-items';
import { Workbasket } from './workbasket';

export class WorkbasketDefinition {
  constructor(
    public distributionTargets: string[],
    public workbasketAccessItems: WorkbasketAccessItems[],
    public workbasket: Workbasket
  ) {
  }
}
