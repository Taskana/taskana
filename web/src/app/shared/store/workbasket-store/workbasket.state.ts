import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { concatMap, take, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { Location } from '@angular/common';
import { WorkbasketService } from '../../services/workbasket/workbasket.service';
import { Workbasket } from '../../models/workbasket';
import {
  CopyWorkbasket,
  CreateWorkbasket,
  DeselectWorkbasket,
  FetchAvailableDistributionTargets,
  FetchWorkbasketDistributionTargets,
  GetWorkbasketAccessItems,
  GetWorkbasketsSummary,
  MarkWorkbasketForDeletion,
  OnButtonPressed,
  RemoveDistributionTarget,
  SaveNewWorkbasket,
  SelectComponent,
  SelectWorkbasket,
  SetActiveAction,
  TransferDistributionTargets,
  UpdateWorkbasket,
  UpdateWorkbasketAccessItems,
  UpdateWorkbasketDistributionTargets
} from './workbasket.actions';
import { WorkbasketSummaryRepresentation } from '../../models/workbasket-summary-representation';
import { ACTION } from '../../models/action';
import { NotificationService } from '../../services/notifications/notification.service';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from '../../models/workbasket-distribution-targets';
import { WorkbasketSummary } from '../../models/workbasket-summary';
import { WorkbasketComponent } from '../../../administration/models/workbasket-component';
import { ButtonAction } from '../../../administration/models/button-action';
import { ActivatedRoute } from '@angular/router';
import { RequestInProgressService } from '../../services/request-in-progress/request-in-progress.service';
import { WorkbasketType } from '../../models/workbasket-type';
import { TaskanaDate } from '../../util/taskana.date';
import { DomainService } from '../../services/domain/domain.service';
import { ClearWorkbasketFilter } from '../filter-store/filter.actions';
import { Injectable } from '@angular/core';
import { WorkbasketQueryPagingParameter } from '../../models/workbasket-query-paging-parameter';
import { Side } from '../../../administration/models/workbasket-distribution-enums';
import { cloneDeep } from 'lodash';

class InitializeStore {
  static readonly type = '[Workbasket] Initializing state';
}

@Injectable()
@State<WorkbasketStateModel>({ name: 'workbasket' })
export class WorkbasketState implements NgxsAfterBootstrap {
  constructor(
    private workbasketService: WorkbasketService,
    private location: Location,
    private notificationService: NotificationService,
    private domainService: DomainService,
    private route: ActivatedRoute,
    private requestInProgressService: RequestInProgressService
  ) {}

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    // read the selected tab from the route
    this.route.queryParams.pipe(take(2)).subscribe((params) => {
      let tabName: string = params.tab;
      let tab: number;

      switch (tabName) {
        case 'information':
          tab = WorkbasketComponent.INFORMATION;
          break;
        case 'access-items':
          tab = WorkbasketComponent.ACCESS_ITEMS;
          break;
        case 'distribution-targets':
          tab = WorkbasketComponent.DISTRIBUTION_TARGETS;
          break;
        default:
          tab = WorkbasketComponent.INFORMATION;
      }

      ctx.patchState({
        badgeMessage: '',
        availableDistributionTargetsPage: 0,
        distributionTargetsPage: 0,
        workbasketDistributionTargets: { distributionTargets: [], _links: {} },
        availableDistributionTargets: { workbaskets: [], _links: {} }
      });

      ctx.dispatch(new SelectComponent(tab));
    });
    return of(null);
  }

  @Action(GetWorkbasketsSummary)
  getWorkbasketsSummary(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketsSummary): Observable<any> {
    ctx.patchState({
      paginatedWorkbasketsSummary: undefined
    });
    return this.workbasketService
      .getWorkBasketsSummary(action.forceRequest, action.filterParameter, action.sortParameter, action.pageParameter)
      .pipe(
        take(1),
        tap((paginatedWorkbasketsSummary) => {
          ctx.patchState({ paginatedWorkbasketsSummary });
        })
      );
  }

  @Action(SelectWorkbasket)
  selectWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: SelectWorkbasket): Observable<any> {
    let selectedComponent;
    switch (ctx.getState().selectedComponent) {
      case WorkbasketComponent.INFORMATION:
        selectedComponent = 'information';
        break;
      case WorkbasketComponent.ACCESS_ITEMS:
        selectedComponent = 'access-items';
        break;
      case WorkbasketComponent.DISTRIBUTION_TARGETS:
        selectedComponent = 'distribution-targets';
        break;
    }

    const id = action.workbasketId;
    if (typeof id !== 'undefined') {
      return this.workbasketService.getWorkBasket(id).pipe(
        take(1),
        tap((selectedWorkbasket) => {
          ctx.patchState({
            selectedWorkbasket,
            action: ACTION.READ,
            badgeMessage: ``
          });

          ctx.dispatch(new GetWorkbasketAccessItems(ctx.getState().selectedWorkbasket._links.accessItems.href));

          this.location.go(
            this.location
              .path()
              .replace(/(workbaskets).*/g, `workbaskets/(detail:${action.workbasketId})?tab=${selectedComponent}`)
          );

          ctx.dispatch(new ClearWorkbasketFilter('selectedDistributionTargets'));
          ctx.dispatch(new ClearWorkbasketFilter('availableDistributionTargets'));
        })
      );
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

  @Action(SetActiveAction)
  setActiveAction(ctx: StateContext<WorkbasketStateModel>, action: SetActiveAction): Observable<any> {
    ctx.patchState({ action: action.action });
    return of(null);
  }

  @Action(SelectComponent)
  selectComponent(ctx: StateContext<WorkbasketStateModel>, action: SelectComponent): Observable<any> {
    switch (action.component) {
      case WorkbasketComponent.INFORMATION:
        ctx.patchState({ selectedComponent: WorkbasketComponent.INFORMATION });
        this.location.go(this.location.path().replace(/(tab).*/g, 'tab=information'));
        break;
      case WorkbasketComponent.ACCESS_ITEMS:
        ctx.patchState({ selectedComponent: WorkbasketComponent.ACCESS_ITEMS });
        this.location.go(this.location.path().replace(/(tab).*/g, 'tab=access-items'));
        break;
      case WorkbasketComponent.DISTRIBUTION_TARGETS:
        ctx.patchState({ selectedComponent: WorkbasketComponent.DISTRIBUTION_TARGETS });
        this.location.go(this.location.path().replace(/(tab).*/g, 'tab=distribution-targets'));
        break;
    }
    return of(null);
  }

  @Action(OnButtonPressed)
  doWorkbasketDetailsAction(ctx: StateContext<WorkbasketStateModel>, action: OnButtonPressed): Observable<any> {
    ctx.patchState({ button: action.button });
    setTimeout(() => {
      ctx.patchState({ button: undefined });
    }, 500);
    return of(null);
  }

  @Action(SaveNewWorkbasket)
  saveNewWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: SaveNewWorkbasket): Observable<any> {
    ctx.dispatch(new OnButtonPressed(undefined));
    return this.workbasketService.createWorkbasket(action.workbasket).pipe(
      take(1),
      tap((workbasketUpdated) => {
        this.notificationService.showSuccess('WORKBASKET_CREATE', { workbasketKey: workbasketUpdated.key });

        this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets'));
      }),
      concatMap((workbasketUpdated) => ctx.dispatch(new SelectWorkbasket(workbasketUpdated.workbasketId)))
    );
  }

  @Action(CopyWorkbasket)
  copyWorkbasket(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets/(detail:new-workbasket)'));
    ctx.dispatch(new OnButtonPressed(undefined));

    const workbasket = { ...ctx.getState().selectedWorkbasket };
    delete workbasket.workbasketId;

    ctx.patchState({
      action: ACTION.COPY,
      selectedWorkbasket: workbasket,
      badgeMessage: `Copying workbasket: ${workbasket.key}`
    });

    ctx.dispatch(new ClearWorkbasketFilter('selectedDistributionTargets'));
    ctx.dispatch(new ClearWorkbasketFilter('availableDistributionTargets'));

    return of(null);
  }

  @Action(CreateWorkbasket)
  createWorkbasket(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    return this.domainService.getSelectedDomain().pipe(
      take(1),
      tap((domain) => {
        this.location.go(this.location.path().replace(/(workbaskets).*/g, 'workbaskets/(detail:new-workbasket)'));

        if (!ctx.getState().availableDistributionTargets) {
          ctx.dispatch(new FetchAvailableDistributionTargets(true));
        }

        const emptyWorkbasket: Workbasket = {};
        emptyWorkbasket.domain = domain;
        emptyWorkbasket.type = WorkbasketType.PERSONAL;

        const date: string = TaskanaDate.getDate();
        emptyWorkbasket.created = date;
        emptyWorkbasket.modified = date;
        emptyWorkbasket.owner = '';

        const accessItems: WorkbasketAccessItemsRepresentation = { accessItems: [], _links: {} };
        const distributionTargets: WorkbasketDistributionTargets = {
          _links: {},
          distributionTargets: []
        };

        ctx.patchState({
          action: ACTION.CREATE,
          selectedWorkbasket: emptyWorkbasket,
          selectedComponent: WorkbasketComponent.INFORMATION,
          badgeMessage: `Creating new workbasket`,
          workbasketAccessItems: accessItems,
          workbasketDistributionTargets: distributionTargets
        });

        ctx.dispatch(new ClearWorkbasketFilter('selectedDistributionTargets'));
        ctx.dispatch(new ClearWorkbasketFilter('availableDistributionTargets'));

        return of(null);
      })
    );
  }

  @Action(UpdateWorkbasket)
  updateWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: UpdateWorkbasket): Observable<any> {
    ctx.dispatch(new OnButtonPressed(undefined));
    return this.workbasketService.updateWorkbasket(action.url, action.workbasket).pipe(
      take(1),
      tap((updatedWorkbasket) => {
        this.notificationService.showSuccess('WORKBASKET_UPDATE', { workbasketKey: updatedWorkbasket.key });

        const paginatedWorkbasketSummary = { ...ctx.getState().paginatedWorkbasketsSummary };
        paginatedWorkbasketSummary.workbaskets = updateWorkbasketSummaryRepresentation(
          paginatedWorkbasketSummary.workbaskets,
          action.workbasket
        );
        ctx.patchState({
          selectedWorkbasket: updatedWorkbasket,
          paginatedWorkbasketsSummary: paginatedWorkbasketSummary
        });
      })
    );
  }

  @Action(RemoveDistributionTarget)
  removeDistributionTarget(ctx: StateContext<WorkbasketStateModel>, action: RemoveDistributionTarget): Observable<any> {
    ctx.dispatch(new OnButtonPressed(undefined));
    return this.workbasketService.removeDistributionTarget(action.url).pipe(
      take(1),
      tap(() => {
        this.notificationService.showSuccess('WORKBASKET_DISTRIBUTION_TARGET_REMOVE', {
          workbasketKey: ctx.getState().selectedWorkbasket.key
        });
      })
    );
  }

  @Action(MarkWorkbasketForDeletion)
  deleteWorkbasket(ctx: StateContext<WorkbasketStateModel>, action: MarkWorkbasketForDeletion): Observable<any> {
    ctx.dispatch(new OnButtonPressed(undefined));
    return this.workbasketService.markWorkbasketForDeletion(action.url).pipe(
      take(1),
      tap((response) => {
        if (response.status !== 202) {
          this.notificationService.showSuccess('WORKBASKET_REMOVE', {
            workbasketKey: ctx.getState().selectedWorkbasket.key
          });

          ctx.dispatch(new DeselectWorkbasket());
        }
      })
    );
  }

  @Action(GetWorkbasketAccessItems)
  getWorkbasketAccessItems(ctx: StateContext<WorkbasketStateModel>, action: GetWorkbasketAccessItems): Observable<any> {
    return this.workbasketService.getWorkBasketAccessItems(action.url).pipe(
      take(1),
      tap((workbasketAccessItemsRepresentation) => {
        ctx.patchState({
          workbasketAccessItems: workbasketAccessItemsRepresentation
        });
      })
    );
  }

  @Action(UpdateWorkbasketAccessItems)
  updateWorkbasketAccessItems(
    ctx: StateContext<WorkbasketStateModel>,
    action: UpdateWorkbasketAccessItems
  ): Observable<any> {
    ctx.dispatch(new OnButtonPressed(undefined));
    return this.workbasketService
      .updateWorkBasketAccessItem(action.url, { accessItems: action.workbasketAccessItems })
      .pipe(
        take(1),
        tap((workbasketAccessItems) => {
          ctx.patchState({
            workbasketAccessItems
          });
          this.notificationService.showSuccess('WORKBASKET_ACCESS_ITEM_SAVE', {
            workbasketKey: ctx.getState().selectedWorkbasket.key
          });
          return of(null);
        })
      );
  }

  @Action(UpdateWorkbasketDistributionTargets)
  updateWorkbasketDistributionTargets(ctx: StateContext<WorkbasketStateModel>): Observable<any> {
    this.requestInProgressService.setRequestInProgress(true);
    return this.workbasketService
      .updateWorkBasketsDistributionTargets(
        ctx.getState().selectedWorkbasket._links.distributionTargets.href,
        ctx.getState().workbasketDistributionTargets.distributionTargets.map((w) => w.workbasketId)
      )
      .pipe(
        take(1),
        tap({
          next: (updatedWorkbasketsDistributionTargets) => {
            ctx.patchState({
              workbasketDistributionTargets: updatedWorkbasketsDistributionTargets
            });
            const workbasketId = ctx.getState().selectedWorkbasket?.workbasketId;

            if (typeof workbasketId !== 'undefined') {
              this.workbasketService.getWorkBasket(workbasketId).subscribe((selectedWorkbasket) => {
                ctx.patchState({
                  selectedWorkbasket,
                  action: ACTION.READ
                });
                ctx.dispatch(new ClearWorkbasketFilter('selectedDistributionTargets'));
                ctx.dispatch(new ClearWorkbasketFilter('availableDistributionTargets'));
              });
            }
            this.requestInProgressService.setRequestInProgress(false);
            this.notificationService.showSuccess('WORKBASKET_DISTRIBUTION_TARGET_SAVE', {
              workbasketName: ctx.getState().selectedWorkbasket.name
            });

            return of(null);
          },
          error: () => {
            this.requestInProgressService.setRequestInProgress(false);
          }
        })
      );
  }

  @Action(FetchWorkbasketDistributionTargets)
  fetchWorkbasketDistributionTargets(
    ctx: StateContext<WorkbasketStateModel>,
    action: FetchWorkbasketDistributionTargets
  ): Observable<any> {
    const { selectedWorkbasket, distributionTargetsPage, workbasketDistributionTargets } = ctx.getState();
    const { filterParameter, sortParameter, refetchAll } = action;
    const nextDistributionTargetsPage = refetchAll ? 1 : distributionTargetsPage + 1;
    return this.workbasketService
      .getWorkBasketsDistributionTargets(
        selectedWorkbasket._links.distributionTargets.href,
        filterParameter,
        sortParameter,
        new WorkbasketQueryPagingParameter(nextDistributionTargetsPage)
      )
      .pipe(
        take(1),
        tap((wbt: WorkbasketDistributionTargets) => {
          if (!refetchAll && workbasketDistributionTargets) {
            wbt.distributionTargets = workbasketDistributionTargets.distributionTargets.concat(wbt.distributionTargets);
          }
          ctx.patchState({
            workbasketDistributionTargets: wbt,
            distributionTargetsPage: nextDistributionTargetsPage
          });
        })
      );
  }

  @Action(FetchAvailableDistributionTargets)
  fetchAvailableDistributionTargets(
    ctx: StateContext<WorkbasketStateModel>,
    action: FetchAvailableDistributionTargets
  ): Observable<any> {
    const { availableDistributionTargetsPage, availableDistributionTargets } = ctx.getState();
    const { filterParameter, sortParameter, refetchAll } = action;
    const nextAvailableDistributionTargetsPage = refetchAll ? 1 : availableDistributionTargetsPage + 1;
    if (!refetchAll && nextAvailableDistributionTargetsPage > availableDistributionTargets.page?.totalPages) {
      return of(null);
    }
    return this.workbasketService
      .getWorkBasketsSummary(
        true,
        filterParameter,
        sortParameter,
        new WorkbasketQueryPagingParameter(nextAvailableDistributionTargetsPage)
      )
      .pipe(
        take(1),
        tap((wbSummaryRepresentation: WorkbasketSummaryRepresentation) => {
          const distributionTargetSet = new Set(
            ctx.getState().workbasketDistributionTargets.distributionTargets.map((wb) => wb.workbasketId)
          );
          wbSummaryRepresentation.workbaskets = wbSummaryRepresentation.workbaskets.filter((wb) => {
            return !distributionTargetSet.has(wb.workbasketId);
          });
          if (!refetchAll && availableDistributionTargets) {
            wbSummaryRepresentation.workbaskets = availableDistributionTargets.workbaskets.concat(
              wbSummaryRepresentation.workbaskets
            );
          }
          ctx.patchState({
            availableDistributionTargets: wbSummaryRepresentation,
            availableDistributionTargetsPage: nextAvailableDistributionTargetsPage
          });
        })
      );
  }

  @Action(TransferDistributionTargets)
  transferDistributionTargets(ctx: StateContext<WorkbasketStateModel>, action: TransferDistributionTargets): void {
    let { workbasketDistributionTargets, availableDistributionTargets } = ctx.getState();
    const workbasketSummarySet = new Set(action.workbasketSummaries.map((wb) => wb.workbasketId));
    availableDistributionTargets = cloneDeep(availableDistributionTargets);
    workbasketDistributionTargets = cloneDeep(workbasketDistributionTargets);
    if (action.targetSide === Side.AVAILABLE) {
      workbasketDistributionTargets.distributionTargets = workbasketDistributionTargets.distributionTargets.filter(
        (wb) => !workbasketSummarySet.has(wb.workbasketId)
      );
      availableDistributionTargets.workbaskets = availableDistributionTargets.workbaskets.concat(
        action.workbasketSummaries
      );
    } else {
      availableDistributionTargets.workbaskets = availableDistributionTargets.workbaskets.filter(
        (wb) => !workbasketSummarySet.has(wb.workbasketId)
      );
      workbasketDistributionTargets.distributionTargets = workbasketDistributionTargets.distributionTargets.concat(
        action.workbasketSummaries
      );
    }
    ctx.patchState({
      availableDistributionTargets,
      workbasketDistributionTargets
    });
  }

  ngxsAfterBootstrap(ctx: StateContext<WorkbasketStateModel>): void {
    ctx.dispatch(new InitializeStore());
  }
}

function updateWorkbasketSummaryRepresentation(
  workbasketsSummary: WorkbasketSummary[],
  selectedWorkbasket: Workbasket
) {
  return workbasketsSummary.map((w) => {
    if (w.workbasketId === selectedWorkbasket.workbasketId) {
      const workbasketSummary: WorkbasketSummary = selectedWorkbasket;
      return workbasketSummary;
    }
    return w;
  });
}

export interface WorkbasketStateModel {
  paginatedWorkbasketsSummary: WorkbasketSummaryRepresentation;
  selectedWorkbasket: Workbasket;
  action: ACTION;
  workbasketAccessItems: WorkbasketAccessItemsRepresentation;
  workbasketDistributionTargets: WorkbasketDistributionTargets;
  distributionTargetsPage: number;
  availableDistributionTargets: WorkbasketSummaryRepresentation;
  availableDistributionTargetsPage: number;
  selectedComponent: WorkbasketComponent;
  badgeMessage: string;
  button: ButtonAction | undefined;
}
