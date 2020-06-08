import { Selector } from '@ngxs/store';
import { WorkbasketState, WorkbasketStateModel } from './workbasket.state';
<<<<<<< HEAD
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { Workbasket } from '../../models/workbasket';
import { ACTION } from '../../models/action';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from '../../models/workbasket-distribution-targets';

export class WorkbasketSelectors {
  // Workbasket
  @Selector([WorkbasketState])
  static selectedWorkbasket(state: WorkbasketStateModel): Workbasket {
    return { ...state.selectedWorkbasket };
  }

  @Selector([WorkbasketState])
  static workbasketsSummary(state: WorkbasketStateModel): WorkbasketSummary[] {
    return state.paginatedWorkbasketsSummary.workbaskets;
  }

  @Selector([WorkbasketState])
  static workbasketsSummaryRepresentation(state: WorkbasketStateModel): WorkbasketSummaryRepresentation {
    return state.paginatedWorkbasketsSummary;
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

  // Workbasket Access Items
  @Selector([WorkbasketState])
  static workbasketAccessItems(state: WorkbasketStateModel): WorkbasketAccessItemsRepresentation {
    return state.workbasketAccessItems;
  }

  // Workbasket Distribution Targets
  @Selector([WorkbasketState])
  static workbasketDistributionTargets(state: WorkbasketStateModel): WorkbasketDistributionTargets {
    return state.workbasketDistributionTargets;
  }
}

export interface WorkbasketAndAction {
  selectedWorkbasket: Workbasket,
  action: ACTION
=======

export class WorkbasketSelectors {
  @Selector([WorkbasketState])
  static workbaskets(state: WorkbasketStateModel) {
    return state.workbaskets;
  }

  @Selector([WorkbasketState])
  static selectedWorkbasket(state: WorkbasketStateModel) {
    return state.selectedWorkbasket;
  }
>>>>>>> TSK-1215 initialized workbasket ngxs store with get all and select workbasket
}
