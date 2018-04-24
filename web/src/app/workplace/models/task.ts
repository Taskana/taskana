import {Classification} from 'app/models/classification';
import {Workbasket} from 'app/models/workbasket';

export class Task {
  constructor(public businessProcessId: string,
              public parentBusinessProcessId: string,
              public owner: string,
              public taskId: string,
              public created: string,    // ISO-8601
              public claimed: string,    // ISO-8601
              public completed: string,  // ISO-8601
              public modified: string,   // ISO-8601
              public planned: string,    // ISO-8601
              public due: string,        // ISO-8601
              public name: string,
              public creator: string,
              public description: string,
              public note: string,
              public state: any,
              public isRead: boolean,
              public isTransferred: boolean,
              public priority: number,
              public classificationSummaryResource: Classification,
              public workbasketSummaryResource: Workbasket) {
  }
}
