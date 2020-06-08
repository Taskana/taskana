import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { WorkbasketService } from '../../services/workbasket/workbasket.service';
import { Workbasket } from '../../models/workbasket';
import { GetWorkbaskets, SelectWorkbasket } from './workbasket.actions';

class InitializeStore {
  static readonly type = '[Workbasket] Initializing state';
}

@State<WorkbasketStateModel>({ name: 'workbasket' })
export class WorkbasketState implements NgxsAfterBootstrap {
  constructor(private workbasketService: WorkbasketService) {
  }

  @Action(GetWorkbaskets)
  getWorkbaskets(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    return this.workbasketService.getAllWorkBaskets().pipe(
      take(1), tap(workbasketResource => {
        ctx.patchState(
          { workbaskets: workbasketResource.workbaskets }
        );
      })
    );
  }

  @Action(SelectWorkbasket)
  selectWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: SelectWorkbasket): Observable<any> {
    const id = action.workbasketId;
    if (typeof id !== 'undefined') {
      return this.workbasketService.getWorkBasket(id).pipe(take(1), tap(
        selectedWorkbasket => {
          ctx.patchState({
            selectedWorkbasket
          });
        }
      ));
    }
    return of(null);
  }

  ngxsAfterBootstrap(ctx?: StateContext<any>): void {
    ctx.dispatch(new InitializeStore());
  }
}

export interface WorkbasketStateModel {
  workbaskets: Workbasket[],
  selectedWorkbasket: Workbasket
}
