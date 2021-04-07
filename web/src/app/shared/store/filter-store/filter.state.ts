import { Action, NgxsOnInit, State, StateContext } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { ClearTaskFilter, ClearWorkbasketFilter, SetTaskFilter, SetWorkbasketFilter } from './filter.actions';
import { TaskQueryFilterParameter } from '../../models/task-query-filter-parameter';

const emptyWorkbasketFilter: WorkbasketQueryFilterParameter = {
  'description-like': [],
  'key-like': [],
  'name-like': [],
  'owner-like': [],
  type: []
};

const emptyTaskFilter: TaskQueryFilterParameter = {
  'name-like': [],
  'owner-like': [],
  state: [],
  priority: [],
  'por.value': [],
  'wildcard-search-fields': [],
  'wildcard-search-value': []
};

@State<FilterStateModel>({ name: 'FilterState' })
export class FilterState implements NgxsOnInit {
  @Action(SetWorkbasketFilter)
  setWorkbasketFilter(ctx: StateContext<FilterStateModel>, action: SetWorkbasketFilter): Observable<null> {
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

  @Action(ClearWorkbasketFilter)
  clearWorkbasketFilter(ctx: StateContext<FilterStateModel>, action: ClearWorkbasketFilter): Observable<null> {
    ctx.setState({
      ...ctx.getState(),
      [action.component]: { ...emptyWorkbasketFilter }
    });

    return of(null);
  }

  @Action(SetTaskFilter)
  setTaskFilter(ctx: StateContext<FilterStateModel>, action: SetTaskFilter): Observable<null> {
    const param = action.parameters;
    let filter = { ...ctx.getState().tasks };

    Object.keys(param).forEach((key) => {
      filter[key] = [...param[key]];
    });

    const isWildcardSearch = filter['wildcard-search-value'].length !== 0 && filter['wildcard-search-value'] !== [''];
    filter['wildcard-search-fields'] = isWildcardSearch ? this.initWildcardFields() : [];

    // Delete wildcard search field 'NAME' if 'name-like' exists
    if (filter['name-like'].length > 0 && filter['name-like'][0] !== '') {
      filter['wildcard-search-fields'].shift();
    }

    ctx.setState({
      ...ctx.getState(),
      tasks: filter
    });

    return of(null);
  }

  @Action(ClearTaskFilter)
  clearTaskFilter(ctx: StateContext<FilterStateModel>): Observable<null> {
    ctx.setState({
      ...ctx.getState(),
      tasks: { ...emptyTaskFilter }
    });

    return of(null);
  }

  initWildcardFields() {
    let wildcardSearchFields = ['NAME', 'DESCRIPTION'];
    [...Array(16).keys()].map((number) => {
      wildcardSearchFields.push(`CUSTOM_${number + 1}`);
    });
    return wildcardSearchFields;
  }

  ngxsOnInit(ctx: StateContext<FilterStateModel>): void {
    ctx.setState({
      ...ctx.getState(),
      availableDistributionTargets: emptyWorkbasketFilter,
      selectedDistributionTargets: emptyWorkbasketFilter,
      workbasketList: emptyWorkbasketFilter,
      tasks: emptyTaskFilter
    });
  }
}

export interface FilterStateModel {
  availableDistributionTargets: WorkbasketQueryFilterParameter;
  selectedDistributionTargets: WorkbasketQueryFilterParameter;
  workbasketList: WorkbasketQueryFilterParameter;
  tasks: TaskQueryFilterParameter;
}
