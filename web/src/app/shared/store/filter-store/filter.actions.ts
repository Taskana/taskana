import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';

export class SetFilter {
  static readonly type = '[Workbasket filter] Set filter parameter';
  constructor(public parameters: WorkbasketQueryFilterParameter, public component: string) {}
}

export class ClearFilter {
  static readonly type = '[Workbasket filter] Clear filter parameter';
  constructor(public component: string) {}
}
