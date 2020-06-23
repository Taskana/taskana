import { Selector } from '@ngxs/store';
import { WorkbasketState, WorkbasketStateModel } from './workbasket.state';
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { Workbasket } from '../../models/workbasket';
import { ACTION } from '../../models/action';

export class WorkbasketSelectors {
  @Selector([WorkbasketState])
  static workbaskets(state: WorkbasketStateModel) {
    return { ...state.workbaskets };
  }

  @Selector([WorkbasketState])
  static selectedWorkbasket(state: WorkbasketStateModel): Workbasket {
    return { ...state.selectedWorkbasket };
  }

  @Selector([WorkbasketState])
  static workbasketsSummary(state: WorkbasketStateModel): WorkbasketSummary[] {
    return state.workbasketsSummary.workbaskets;
  }

  @Selector([WorkbasketState])
  static workbasketsSummaryRepresentation(state: WorkbasketStateModel): WorkbasketSummaryRepresentation {
    return state.workbasketsSummary;
  }

  @Selector([WorkbasketState])
  static workbasketActiveAction(state: WorkbasketStateModel): ACTION {
    return state.action;
  }

  @Selector([WorkbasketState])
  static selectedWorkbasketAndAction(state: WorkbasketStateModel): WorkbasketAndAction {
    return {
      selectedWorkbasket: state.selectedWorkbasket,
      action: state.action
    };
  }
}

export interface WorkbasketAndAction {
  selectedWorkbasket: Workbasket,
  action: ACTION
}
