import {Classification} from 'app/models/classification';
import {Workbasket} from 'app/models/workbasket';
import {ObjectReference} from './object-reference';

export class Task {
  constructor(public taskId: string,
              public primaryObjRef: ObjectReference = undefined,
              public workbasketSummaryResource: Workbasket = undefined,
              public classificationSummaryResource: Classification = undefined,
              public businessProcessId: string = undefined,
              public parentBusinessProcessId: string = undefined,
              public owner: string = undefined,
              public created: string = undefined,    // ISO-8601
              public claimed: string = undefined,    // ISO-8601
              public completed: string = undefined,  // ISO-8601
              public modified: string = undefined,   // ISO-8601
              public planned: string = undefined,    // ISO-8601
              public due: string = undefined,        // ISO-8601
              public name: string = undefined,
              public creator: string = undefined,
              public description: string = undefined,
              public note: string = undefined,
              public state: any = undefined,
              public read: boolean = undefined,
              public transferred: boolean = undefined,
              public priority: number = undefined,
              public customAttributes: Array<CustomAttribute> = [],
              public callbackInfo: Array<CustomAttribute> = [],
              public custom1: string = undefined,
              public custom2: string = undefined,
              public custom3: string = undefined,
              public custom4: string = undefined,
              public custom5: string = undefined,
              public custom6: string = undefined,
              public custom7: string = undefined,
              public custom8: string = undefined,
              public custom9: string = undefined,
              public custom10: string = undefined,
              public custom11: string = undefined,
              public custom12: string = undefined,
              public custom13: string = undefined,
              public custom14: string = undefined,
              public custom15: string = undefined,
              public custom16: string = undefined) {
  }
}

export class CustomAttribute {
  key: string;
  value: string;
}
