import { Workbasket } from '../../models/workbasket';

export class GetWorkbaskets {
  static readonly type = '[Workbasket] Get all workbaskets';
}

export class SelectWorkbasket {
  static readonly type = '[Workbasket] Select a workbasket';
  constructor(public workbasketId: string) {
  }
}

export class CreateWorkbasket {
  static readonly type = '[Workbasket] Create a workbasket';
  constructor(public workbasket: Workbasket) {
  }
}

export class GetWorkbasketAccessItems {
  static readonly type = '[Workbasket] Get all workbasket access items';
  constructor(public url: string) {
  }
}
