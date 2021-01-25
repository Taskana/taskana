import { Selector } from '@ngxs/store';
import { WorkbasketState, WorkbasketStateModel } from './workbasket.state';
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { ACTION } from '../../models/action';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from '../../models/workbasket-distribution-targets';
import { Workbasket } from '../../models/workbasket';
import { WorkbasketComponent } from '../../../administration/models/workbasket-component';
import { ButtonAction } from '../../../administration/models/button-action';

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

  @Selector([WorkbasketState])
  static selectedWorkbasketAndComponentAndAction(state: WorkbasketStateModel): WorkbasketAndComponentAndAction {
    return {
      selectedWorkbasket: state.selectedWorkbasket,
      action: state.action,
      selectedComponent: state.selectedComponent
    };
  }

  @Selector([WorkbasketState])
  static selectedComponent(state: WorkbasketStateModel): WorkbasketComponent {
    return state.selectedComponent;
  }

  @Selector([WorkbasketState])
  static buttonAction(state: WorkbasketStateModel): ButtonAction {
    return state.button;
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

  @Selector([WorkbasketState])
  static availableDistributionTargets(state: WorkbasketStateModel): WorkbasketSummary[] {
    return state.workbasketAvailableDistributionTargets;
  }

  @Selector([WorkbasketState])
  static badgeMessage(state: WorkbasketStateModel): string {
    return state.badgeMessage;
  }
}

export interface WorkbasketAndAction {
  selectedWorkbasket: Workbasket;
  action: ACTION;
}

export interface WorkbasketAndComponentAndAction {
  selectedWorkbasket: Workbasket;
  action: ACTION;
  selectedComponent: WorkbasketComponent;
}
