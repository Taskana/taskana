import { Selector } from '@ngxs/store';
import { WorkbasketState, WorkbasketStateModel } from './workbasket.state';

export class WorkbasketSelectors {
  @Selector([WorkbasketState])
  static workbaskets(state: WorkbasketStateModel) {
    return state.workbaskets;
  }

  @Selector([WorkbasketState])
  static selectedWorkbasket(state: WorkbasketStateModel) {
    return state.selectedWorkbasket;
  }
}
