import { Action, NgxsOnInit, State, StateContext } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { CalculateNumberOfCards, SetFilterExpansion } from './workplace.actions';

@State<WorkplaceStateModel>({ name: 'WorkplaceState' })
export class WorkplaceState implements NgxsOnInit {
  @Action(SetFilterExpansion)
  setFilterExpansion(ctx: StateContext<WorkplaceStateModel>, action: SetFilterExpansion): Observable<null> {
    const param = action.isExpanded;
    const isExpanded = typeof param !== 'undefined' ? param : !ctx.getState().isFilterExpanded;

    ctx.setState({
      ...ctx.getState(),
      isFilterExpanded: isExpanded
    });

    ctx.dispatch(new CalculateNumberOfCards());

    return of(null);
  }

  @Action(CalculateNumberOfCards)
  calculateNumberOfCards(ctx: StateContext<WorkplaceStateModel>): Observable<null> {
    const cardHeight = 90;
    const totalHeight = window.innerHeight;
    const toolbarHeight = ctx.getState().isFilterExpanded ? 308 : 192;
    const occupiedHeight = 56 + 90 + toolbarHeight;

    const cards = Math.max(1, Math.round((totalHeight - occupiedHeight) / cardHeight));

    ctx.setState({
      ...ctx.getState(),
      cards: cards
    });

    return of(null);
  }

  ngxsOnInit(ctx: StateContext<WorkplaceStateModel>): void {
    this.calculateNumberOfCards(ctx);

    ctx.setState({
      ...ctx.getState(),
      isFilterExpanded: false
    });
  }
}

export interface WorkplaceStateModel {
  isFilterExpanded: boolean;
  cards: number;
}
