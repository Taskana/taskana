import { FilterState, FilterStateModel } from './filter.state';
import { Selector } from '@ngxs/store';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { TaskQueryFilterParameter } from '../../models/task-query-filter-parameter';

export class FilterSelectors {
  @Selector([FilterState])
  static getAvailableDistributionTargetsFilter(state: FilterStateModel): WorkbasketQueryFilterParameter {
    return state.availableDistributionTargets;
  }

  @Selector([FilterState])
  static getSelectedDistributionTargetsFilter(state: FilterStateModel): WorkbasketQueryFilterParameter {
    return state.selectedDistributionTargets;
  }

  @Selector([FilterState])
  static getWorkbasketListFilter(state: FilterStateModel): WorkbasketQueryFilterParameter {
    return state.workbasketList;
  }

  @Selector([FilterState])
  static getTaskFilter(state: FilterStateModel): TaskQueryFilterParameter {
    return state.tasks;
  }
}
