import { Selector } from '@ngxs/store';
import { WorkplaceState, WorkplaceStateModel } from './workplace.state';

export class WorkplaceSelectors {
  @Selector([WorkplaceState])
  static getFilterExpansion(state: WorkplaceStateModel): boolean {
    return state.isFilterExpanded;
  }

  @Selector([WorkplaceState])
  static getNumberOfCards(state: WorkplaceStateModel): number {
    return state.cards;
  }
}
