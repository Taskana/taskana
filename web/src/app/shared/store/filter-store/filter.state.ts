import { Action, NgxsOnInit, State, StateContext } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { ClearFilter, SetFilter } from './filter.actions';

const emptyFilter: WorkbasketQueryFilterParameter = {
  'description-like': [],
  'key-like': [],
  'name-like': [],
  'owner-like': [],
  type: []
};

@State<FilterStateModel>({ name: 'FilterState' })
export class FilterState implements NgxsOnInit {
  @Action(SetFilter)
  setAvailableDistributionTargetsFilter(ctx: StateContext<FilterStateModel>, action: SetFilter): Observable<null> {
    const currentState = ctx.getState()[action.component];
    const param = action.parameters;
    const filter: WorkbasketQueryFilterParameter = {
      'description-like': param['description-like'] ? [...param['description-like']] : currentState['description-like'],
      'key-like': param['key-like'] ? [...param['key-like']] : currentState['key-like'],
      'name-like': param['name-like'] ? [...param['name-like']] : currentState['name-like'],
      'owner-like': param['owner-like'] ? [...param['owner-like']] : currentState['owner-like'],
      type: param['type'] ? [...param['type']] : currentState['type']
    };

    ctx.setState({
      ...ctx.getState(),
      [action.component]: filter
    });

    return of(null);
  }

  @Action(ClearFilter)
  clearFilter(ctx: StateContext<FilterStateModel>, action: ClearFilter): Observable<null> {
    ctx.setState({
      ...ctx.getState(),
      [action.component]: { ...emptyFilter }
    });

    return of(null);
  }

  ngxsOnInit(ctx: StateContext<FilterStateModel>): void {
    ctx.setState({
      ...ctx.getState(),
      availableDistributionTargets: emptyFilter,
      selectedDistributionTargets: emptyFilter,
      workbasketList: emptyFilter
    });
  }
}

export interface FilterStateModel {
  availableDistributionTargets: WorkbasketQueryFilterParameter;
  selectedDistributionTargets: WorkbasketQueryFilterParameter;
  workbasketList: WorkbasketQueryFilterParameter;
}
