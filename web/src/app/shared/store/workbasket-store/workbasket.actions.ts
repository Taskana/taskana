import { Workbasket } from '../../models/workbasket';
import { Direction, Sorting, WorkbasketQuerySortParameter } from '../../models/sorting';
import { ACTION } from '../../models/action';
import { WorkbasketAccessItems } from '../../models/workbasket-access-items';
import { WorkbasketComponent } from '../../../administration/models/workbasket-component';
import { ButtonAction } from '../../../administration/models/button-action';
import { QueryPagingParameter } from '../../models/query-paging-parameter';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { Side } from '../../../administration/models/workbasket-distribution-enums';

// Workbasket List
export class GetWorkbasketsSummary {
  static readonly type = "[Workbasket List] Get all workbaskets' summary";

  constructor(
    public forceRequest: boolean = false,
    public filterParameter: WorkbasketQueryFilterParameter,
    public sortParameter: Sorting<WorkbasketQuerySortParameter>,
    public pageParameter: QueryPagingParameter
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

export class MarkWorkbasketForDeletion {
  static readonly type = '[Workbasket] Mark selected workbasket for deletion';

  constructor(public url: string) {}
}

export class RemoveDistributionTarget {
  static readonly type = '[Workbasket] Remove selected workbasket as distribution target';

  constructor(public url: string) {}
}

// Workbasket Access Items
export class GetWorkbasketAccessItems {
  static readonly type = '[Workbasket] Get all workbasket access items';

  constructor(public url: string) {}
}

export class UpdateWorkbasketAccessItems {
  static readonly type = '[Workbasket] Update selected workbaskets access items';

  constructor(public url: string, public workbasketAccessItems: WorkbasketAccessItems[]) {}
}

export class UpdateWorkbasketDistributionTargets {
  static readonly type = '[Workbasket] Update workbasket distribution targets';
}

export class FetchWorkbasketDistributionTargets {
  static readonly type = '[Workbasket] Fetch a subset of selected workbasket distribution targets';

  constructor(
    public refetchAll: boolean,
    public filterParameter?: WorkbasketQueryFilterParameter,
    public sortParameter: Sorting<WorkbasketQuerySortParameter> = {
      'sort-by': WorkbasketQuerySortParameter.NAME,
      order: Direction.ASC
    }
  ) {}
}

export class FetchAvailableDistributionTargets {
  static readonly type = '[Workbasket] Fetch a subset of available workbasket distribution targets';

  constructor(
    public refetchAll: boolean,
    public filterParameter?: WorkbasketQueryFilterParameter,
    public sortParameter?: Sorting<WorkbasketQuerySortParameter>
  ) {}
}

export class TransferDistributionTargets {
  static readonly type = '[Workbasket] Transfer a set of workbasket distribution targets';

  constructor(public targetSide: Side, public workbasketSummaries: WorkbasketSummary[]) {}
}
