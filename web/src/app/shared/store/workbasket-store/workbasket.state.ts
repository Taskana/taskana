import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { WorkbasketService } from '../../services/workbasket/workbasket.service';
import { Workbasket } from '../../models/workbasket';
import { CreateWorkbasket,
  GetWorkbasketAccessItems,
  GetWorkbaskets,
  GetWorkbasketsSummary,
  SelectWorkbasket, UpdateWorkbasket } from './workbasket.actions';
import { WorkbasketAccessItems } from '../../models/workbasket-access-items';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';

class InitializeStore {
  static readonly type = '[Workbasket] Initializing state';
}

@State<WorkbasketStateModel>({ name: 'workbasket' })
export class WorkbasketState implements NgxsAfterBootstrap {
  constructor(private workbasketService: WorkbasketService) {
  }

  @Action(GetWorkbasketsSummary)
  getWorkbasketsSummary(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketsSummary): Observable<any> {
    return this.workbasketService.getWorkBasketsSummary(action.forceRequest,
      action.sortBy, action.order, action.name, action.nameLike, action.descLike, action.owner, action.ownerLike,
      action.type, action.key, action.keyLike, action.requiredPermission, action.allPages).pipe(
      take(1), tap(workbasketsSummary => {
        ctx.patchState(
          { workbasketsSummary }
        );
      })
    );
  }

  @Action(GetWorkbaskets)
  getWorkbaskets(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    return this.workbasketService.getAllWorkBaskets().pipe(
      take(1), tap(workbasketRepresentation => {
        ctx.patchState(
          { workbaskets: workbasketRepresentation }
        );
      })
    );
  }

  @Action(GetWorkbasketAccessItems)
  getWorkbasketAccessItems(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketAccessItems): Observable<any> {
    return this.workbasketService.getWorkBasketAccessItems(action.url).pipe(take(1), tap(
      workbasketAccessItemsResource => {
        ctx.patchState({
          workbasketAccessItems: workbasketAccessItemsResource.accessItems
        });
      }
    ));
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

  @Action(CreateWorkbasket)
  createWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: CreateWorkbasket): Observable<any> {
    return this.workbasketService.createWorkbasket(action.workbasket).pipe(take(1), tap(
      () => {
        this.getWorkbaskets(ctx);
      }
    ));
  }

  @Action(UpdateWorkbasket)
  updateWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: UpdateWorkbasket): Observable<any> {
    return this.workbasketService.updateWorkbasket(action.url, action.workbasket).pipe(take(1), tap(
      () => {
        this.getWorkbaskets(ctx);
      }
    ));
  }

  ngxsAfterBootstrap(ctx?: StateContext<any>): void {
    ctx.dispatch(new InitializeStore());
  }
}

export interface WorkbasketStateModel {
  workbasketsSummary: WorkbasketSummaryRepresentation,
  workbaskets: Workbasket[],
  selectedWorkbasket: Workbasket,
  workbasketAccessItems: WorkbasketAccessItems
}
