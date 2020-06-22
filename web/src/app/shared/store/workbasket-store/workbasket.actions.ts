import { Workbasket } from '../../models/workbasket';
import { TaskanaQueryParameters } from '../../util/query-parameters';
import { Direction } from '../../models/sorting';
import { ACTION } from '../../models/action';

export class GetWorkbasketsSummary {
  static readonly type = '[Workbasket List] Get all workbaskets\' summary';

  constructor(public forceRequest: boolean = false,
    public sortBy: string = TaskanaQueryParameters.parameters.KEY,
    public order: string = Direction.ASC,
    public name?: string,
    public nameLike?: string,
    public descLike?: string,
    public owner?: string,
    public ownerLike?: string,
    public type?: string,
    public key?: string,
    public keyLike?: string,
    public requiredPermission?: string,
    public allPages: boolean = false) {
  }
}

export class GetWorkbaskets {
  static readonly type = '[Workbasket] Get all workbaskets';
}

export class GetWorkbasketAccessItems {
  static readonly type = '[Workbasket] Get all workbasket access items';
  constructor(public url: string) {
  }
}

export class SelectWorkbasket {
  static readonly type = '[Workbasket] Select a workbasket';
  constructor(public workbasketId: string) {
  }
}

export class CreateWorkbasket {
  static readonly type = '[Workbasket] Create a workbasket';
}

export class SaveNewWorkbasket {
  static readonly type = '[Workbasket] Save new workbasket';
  constructor(public workbasket: Workbasket) {
  }
}

export class UpdateWorkbasket {
  static readonly type = '[Workbasket] Update a workbasket';
  constructor(public url: string, public workbasket: Workbasket) {
  }
}

export class SetActiveAction {
  static readonly type = '[Workbasket] Specify current action';
  constructor(public action: ACTION) {
  }
}
