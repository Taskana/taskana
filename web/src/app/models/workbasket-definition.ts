/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-useless-constructor */
import { WorkbasketAccessItems } from './workbasket-access-items';
import { Workbasket } from './workbasket';

export class WorkbasketDefinition {
  constructor(distributionTargets: string[],
    workbasketAccessItems: WorkbasketAccessItems[],
    workbasket: Workbasket) {
  }
}
