import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { TaskQueryFilterParameter } from '../../models/task-query-filter-parameter';

// Workbasket Filter
export class SetWorkbasketFilter {
  static readonly type = '[Workbasket filter] Set workbasket filter parameter';
  constructor(public parameters: WorkbasketQueryFilterParameter, public component: string) {}
}

export class ClearWorkbasketFilter {
  static readonly type = '[Workbasket filter] Clear workbasket filter parameter';
  constructor(public component: string) {}
}

// Task Filter
export class SetTaskFilter {
  static readonly type = '[Task filter] Set task filter parameter';
  constructor(public parameters: TaskQueryFilterParameter) {}
}

export class ClearTaskFilter {
  static readonly type = '[Task filter] Clear task filter parameter';
}
