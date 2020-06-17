import { Selector } from '@ngxs/store';
import { WorkbasketState, WorkbasketStateModel } from './workbasket.state';
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { Workbasket } from '../../models/workbasket';

export class WorkbasketSelectors {
  @Selector([WorkbasketState])
  static workbaskets(state: WorkbasketStateModel) {
    return state.workbaskets;
  }

  @Selector([WorkbasketState])
  static selectedWorkbasket(state: WorkbasketStateModel): Workbasket {
    return state.selectedWorkbasket;
  }

  @Selector([WorkbasketState])
  static workbasketsSummary(state: WorkbasketStateModel): WorkbasketSummary[] {
    return state.workbasketsSummary.workbaskets;
  }

  @Selector([WorkbasketState])
  static workbasketsSummaryRepresentation(state: WorkbasketStateModel): WorkbasketSummaryRepresentation {
    return state.workbasketsSummary;
  }
}
