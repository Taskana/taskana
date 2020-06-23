import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { Location } from '@angular/common';
import { WorkbasketService } from '../../services/workbasket/workbasket.service';
import { Workbasket } from '../../models/workbasket';
import { CopyWorkbasket, CreateWorkbasket, DeleteWorkbasket, DeselectWorkbasket,
  GetWorkbasketAccessItems,
  GetWorkbaskets,
  GetWorkbasketsSummary, RemoveDistributionTarget, SaveNewWorkbasket,
  SelectWorkbasket, SetActiveAction, UpdateWorkbasket } from './workbasket.actions';
import { WorkbasketAccessItems } from '../../models/workbasket-access-items';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { ACTION } from '../../models/action';
import { DomainService } from '../../services/domain/domain.service';

class InitializeStore {
  static readonly type = '[Workbasket] Initializing state';
}

@State<WorkbasketStateModel>({ name: 'workbasket' })
export class WorkbasketState implements NgxsAfterBootstrap {
  constructor(
    private workbasketService: WorkbasketService,
    private domainService: DomainService,
    private location: Location
  ) {
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
    this.location.go(this.location.path().replace(/(workbaskets).*/g, `workbaskets/(detail:${action.workbasketId})`));
    const id = action.workbasketId;
    if (typeof id !== 'undefined') {
      return this.workbasketService.getWorkBasket(id).pipe(take(1), tap(
        selectedWorkbasket => {
          ctx.patchState({
            selectedWorkbasket,
            action: ACTION.READ
          });
        }
      ));
    }
    return of(null);
  }

  @Action(DeselectWorkbasket)
  deselectWorkbasket(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets'));
    ctx.patchState({
      selectedWorkbasket: undefined,
      action: ACTION.READ
    });
    return of(null);
  }

  @Action(CreateWorkbasket)
  createWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: CreateWorkbasket): Observable<any> {
    this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets/(detail:new-workbasket)'));
    ctx.patchState({
      selectedWorkbasket: undefined,
      action: ACTION.CREATE
    });
    return of(null);
  }

  @Action(SaveNewWorkbasket)
  saveNewWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: SaveNewWorkbasket): Observable<any> {
    return this.workbasketService.createWorkbasket(action.workbasket).pipe(take(1), tap(
      () => {
        this.getWorkbaskets(ctx);
      }
    ));
  }

  @Action(CopyWorkbasket)
  copyWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: CopyWorkbasket): Observable<any> {
    const workbasketCopy = action.workbasket;
    ctx.patchState({
      workbasketCopy,
      action: ACTION.COPY
    });
    return of(null);
  }

  @Action(UpdateWorkbasket)
  updateWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: UpdateWorkbasket): Observable<any> {
    return this.workbasketService.updateWorkbasket(action.url, action.workbasket).pipe(take(1), tap(
      () => {
        this.getWorkbaskets(ctx);
      }
    ));
  }

  @Action(SetActiveAction)
  setActiveAction(ctx: StateContext<WorkbasketStateModel>, action: SetActiveAction): Observable<any> {
    ctx.patchState({ action: action.action });
    return of(null);
  }

  @Action(RemoveDistributionTarget)
  removeDistributionTarget(ctx: StateContext<WorkbasketStateModel>, action: RemoveDistributionTarget): Observable<any> {
    return this.workbasketService.removeDistributionTarget(action.url).pipe(take(1), tap(
      () => {

      }
    ));
  }

  @Action(DeleteWorkbasket)
  deleteWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: DeleteWorkbasket): Observable<any> {
    return this.workbasketService.markWorkbasketForDeletion(action.url).pipe(take(1), map(
      () => {

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
  workbasketCopy: Workbasket,
  action: ACTION,
  workbasketAccessItems: WorkbasketAccessItems
}