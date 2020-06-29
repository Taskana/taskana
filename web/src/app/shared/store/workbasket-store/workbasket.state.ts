import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { Location } from '@angular/common';
import { WorkbasketService } from '../../services/workbasket/workbasket.service';
import { Workbasket } from '../../models/workbasket';
import { CopyWorkbasket,
  CreateWorkbasket,
  DeselectWorkbasket,
  GetWorkbasketAccessItems,
  GetWorkbasketDistributionTargets,
  GetWorkbaskets,
  GetWorkbasketsSummary,
  MarkWorkbasketForDeletion,
  RemoveDistributionTarget,
  SaveNewWorkbasket,
  SelectWorkbasket,
  SetActiveAction,
  UpdateWorkbasket,
  UpdateWorkbasketDistributionTargets,
  UpdateworkbasketSummaryParams } from './workbasket.actions';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { ACTION } from '../../models/action';
import { DomainService } from '../../services/domain/domain.service';
import { NOTIFICATION_TYPES } from '../../models/notifications';
import { NotificationService } from '../../services/notifications/notification.service';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from '../../models/workbasket-distribution-targets';

class InitializeStore {
  static readonly type = '[Workbasket] Initializing state';
}

@State<WorkbasketStateModel>({ name: 'workbasket' })
export class WorkbasketState implements NgxsAfterBootstrap {
  constructor(
    private workbasketService: WorkbasketService,
    private domainService: DomainService,
    private location: Location,
    private notificationService: NotificationService
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
  createWorkbasket(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets/(detail:new-workbasket)'));
    ctx.patchState({
      selectedWorkbasket: undefined,
      action: ACTION.CREATE
    });
    return of(null);
  }

  @Action(SetActiveAction)
  setActiveAction(ctx: StateContext<WorkbasketStateModel>, action: SetActiveAction): Observable<any> {
    ctx.patchState({ action: action.action });
    return of(null);
  }

  @Action(SaveNewWorkbasket)
  saveNewWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: SaveNewWorkbasket): Observable<any> {
    return this.workbasketService.createWorkbasket(action.workbasket).pipe(take(1), tap(
      workbasketUpdated => {
        this.notificationService.showToast(
          NOTIFICATION_TYPES.SUCCESS_ALERT_11,
          new Map<string, string>([['workbasketKey', workbasketUpdated.key]])
        );
        this.selectWorkbasket(ctx, workbasketUpdated.workbasketId);
        this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets'));
      }, error => {
        this.notificationService.triggerError(NOTIFICATION_TYPES.CREATE_ERR_2, error);
      }
    ));
  }

  @Action(CopyWorkbasket)
  copyWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: CopyWorkbasket): Observable<any> {
    const workbasketCopy = action.workbasket;
    delete workbasketCopy.key;
    this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets/(detail:new-workbasket)'));
    ctx.patchState({
      workbasketCopy,
      action: ACTION.COPY
    });
    return of(null);
  }

  @Action(UpdateWorkbasket)
  updateWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: UpdateWorkbasket): Observable<any> {
    return this.workbasketService.updateWorkbasket(action.url, action.workbasket).pipe(take(1), tap(
      updatedWorkbasket => {
        this.notificationService.showToast(
          NOTIFICATION_TYPES.SUCCESS_ALERT_10,
          new Map<string, string>([['workbasketKey', updatedWorkbasket.key]])
        );
        ctx.patchState({
          selectedWorkbasket: updatedWorkbasket,
        });
      }
    ));
  }

  @Action(RemoveDistributionTarget)
  removeDistributionTarget(ctx: StateContext<WorkbasketStateModel>, action: RemoveDistributionTarget): Observable<any> {
    return this.workbasketService.removeDistributionTarget(action.url).pipe(take(1), tap(
      () => {
        this.notificationService.showToast(
          NOTIFICATION_TYPES.SUCCESS_ALERT_9,
          new Map<string, string>([['workbasketId', ctx.getState().selectedWorkbasket.workbasketId]])
        );
      }, error => {
        this.notificationService.triggerError(NOTIFICATION_TYPES.REMOVE_ERR_2,
          error,
          new Map<String, String>([['workbasketId', ctx.getState().selectedWorkbasket.workbasketId]]));
      }
    ));
  }

  @Action(MarkWorkbasketForDeletion)
  deleteWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: MarkWorkbasketForDeletion): Observable<any> {
    return this.workbasketService.markWorkbasketForDeletion(action.url).pipe(take(1), tap(
      response => {
        this.workbasketService.triggerWorkBasketSaved();
        if (response.status === 202) {
          this.notificationService.triggerError(NOTIFICATION_TYPES.MARK_ERR,
            undefined,
            new Map<String, String>([['workbasketId', ctx.getState().selectedWorkbasket.workbasketId]]));
        } else {
          this.notificationService.showToast(
            NOTIFICATION_TYPES.SUCCESS_ALERT_12,
            new Map<string, string>([['workbasketId', ctx.getState().selectedWorkbasket.workbasketId]])
          );
        }
      }
    ));
  }

  @Action(UpdateworkbasketSummaryParams)
  triggerUpdateWorkbasketList(ctx: StateContext<WorkbasketStateModel>, action: UpdateworkbasketSummaryParams): Observable<any> {
    return of(null);
  }

  @Action(GetWorkbasketAccessItems)
  getWorkbasketAccessItems(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketAccessItems): Observable<any> {
    return this.workbasketService.getWorkBasketAccessItems(action.url).pipe(take(1), tap(
      workbasketAccessItemsRepresentation => {
        ctx.patchState({
          workbasketAccessItems: workbasketAccessItemsRepresentation
        });
      }
    ));
  }

  @Action(GetWorkbasketDistributionTargets)
  getWorkbasketDistributionTargets(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketDistributionTargets): Observable<any> {
    return this.workbasketService.getWorkBasketsDistributionTargets(action.url).pipe(take(1), tap(
      workbasketDistributionTargets => {
        ctx.patchState({
          workbasketDistributionTargets
        });
      }
    ));
  }

  @Action(UpdateWorkbasketDistributionTargets)
  updateWorkbasketDistributionTargets(
    ctx: StateContext<WorkbasketStateModel>,
    action: UpdateWorkbasketDistributionTargets
  ): Observable<any> {
    return this.workbasketService.updateWorkBasketsDistributionTargets(action.url, action.distributionTargetsIds).pipe(take(1), tap(
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
  workbasketAccessItems: WorkbasketAccessItemsRepresentation,
  workbasketDistributionTargets: WorkbasketDistributionTargets
}
