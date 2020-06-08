
export class GetWorkbaskets {
  static readonly type = '[Workbasket] Get all workbaskets';
}

export class SelectWorkbasket {
  static readonly type = '[Workbasket] Select a workbasket';
  constructor(public workbasketId: string) {
  }
}
