import { Workbasket } from '../../models/workbasket';
import { TaskanaQueryParameters } from '../../util/query-parameters';
import { Direction } from '../../models/sorting';
import { ACTION } from '../../models/action';
import { WorkbasketAccessItems } from '../../models/workbasket-access-items';
import { WorkbasketComponent } from '../../../administration/models/workbasket-component';
import { ButtonAction } from '../../../administration/models/button-action';

// Workbasket List
export class GetWorkbasketsSummary {
  static readonly type = "[Workbasket List] Get all workbaskets' summary";

  constructor(
    public forceRequest: boolean = false,
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
    public allPages: boolean = false
  ) {}
}

export class SelectWorkbasket {
  static readonly type = '[Workbasket] Select a workbasket';
  constructor(public workbasketId: string) {}
}

export class DeselectWorkbasket {
  static readonly type = '[Workbasket] Deselect workbasket';
}

export class CreateWorkbasket {
  static readonly type = '[Workbasket] Create new workbasket';
}

export class SetActiveAction {
  static readonly type = '[Workbasket] Specify current action';
  constructor(public action: ACTION) {}
}

//Workbasket Details
export class SelectComponent {
  static readonly type = '[Workbasket] Select component';
  constructor(public component: WorkbasketComponent) {}
}

export class OnButtonPressed {
  static readonly type = '[Workbasket] Button pressed';
  constructor(public button: ButtonAction) {}
}

// Workbasket Information
export class SaveNewWorkbasket {
  static readonly type = '[Workbasket] Save new workbasket';
  constructor(public workbasket: Workbasket) {}
}

export class CopyWorkbasket {
  static readonly type = '[Workbasket] Copy selected workbasket';
  constructor(public workbasket: Workbasket) {}
}

export class UpdateWorkbasket {
  static readonly type = '[Workbasket] Update a workbasket';
  constructor(public url: string, public workbasket: Workbasket) {}
}

export class RemoveDistributionTarget {
  static readonly type = '[Workbasket] Remove distribution targets of selected workbasket';
  constructor(public url: string) {}
}

export class MarkWorkbasketForDeletion {
  static readonly type = '[Workbasket] Mark selected workbasket for deletion';
  constructor(public url: string) {}
}

// Workbasket Access Items
export class GetWorkbasketAccessItems {
  static readonly type = '[Workbasket] Get all workbasket access items';
  constructor(public url: string) {}
}

export class UpdateWorkbasketAccessItems {
  static readonly type = "[Workbasket] Update selected workbasket's access items";
  constructor(public url: string, public workbasketAccessItems: WorkbasketAccessItems[]) {}
}

// Workbasket Distribution Targets
export class GetWorkbasketDistributionTargets {
  static readonly type = '[Workbasket] Get all workbasket distribution targets';
  constructor(public url: string) {}
}

export class UpdateWorkbasketDistributionTargets {
  static readonly type = '[Workbasket] Update workbasket distribution targets';
  constructor(public url: string, public distributionTargetsIds: string[]) {}
}
