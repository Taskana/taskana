import { Component, ElementRef, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';
import { ACTION } from 'app/shared/models/action';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Page } from 'app/shared/models/page';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { Orientation } from 'app/shared/models/orientation';
import { Select, Store } from '@ngxs/store';
import { take, takeUntil } from 'rxjs/operators';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import {
  GetWorkbasketDistributionTargets,
  GetWorkbasketsSummary,
  UpdateWorkbasketDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { WorkbasketStateModel } from '../../../shared/store/workbasket-store/workbasket.state';
import { WorkbasketDistributionTargetsListDialogComponent } from '../workbasket-distribution-targets-list-dialog/workbasket-distribution-targets-list-dialog.component';
import { MatDialog } from '@angular/material/dialog';

export enum Side {
  LEFT,
  RIGHT
}
@Component({
  selector: 'taskana-administration-workbasket-distribution-targets',
  templateUrl: './workbasket-distribution-targets.component.html',
  styleUrls: ['./workbasket-distribution-targets.component.scss']
})
export class WorkbasketDistributionTargetsComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  workbasket: Workbasket;

  @Input()
  action: ACTION;

  badgeMessage = '';
  selectedId = '';
  toolbarState = false;

  distributionTargetsSelectedResource: WorkbasketDistributionTargets;
  distributionTargetsLeft: Array<WorkbasketSummary> = [];
  distributionTargetsRight: Array<WorkbasketSummary> = [];
  distributionTargetsSelected: Array<WorkbasketSummary>;
  distributionTargetsClone: Array<WorkbasketSummary>;
  distributionTargetsSelectedClone: Array<WorkbasketSummary>;

  requestInProgressLeft = false;
  requestInProgressRight = false;
  loadingItems = false;
  side = Side;
  private initialized = false;
  page: Page;
  cards: number;
  selectAllLeft = false;
  selectAllRight = false;

  @ViewChild('panelBody')
  panelBody: ElementRef;

  @Select(WorkbasketSelectors.workbasketDistributionTargets)
  workbasketDistributionTargets$: Observable<WorkbasketDistributionTargets>;

  destroy$ = new Subject<void>();

  constructor(
    private workbasketService: WorkbasketService,
    private savingWorkbaskets: SavingWorkbasketService,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService,
    private notificationsService: NotificationService,
    private store: Store,
    public matDialog: MatDialog
  ) {}

  ngOnInit() {
    console.log('distribution targets init');
    this.init();
    this.workbasketDistributionTargets$.subscribe((workbasketDistributionTargets) => {
      if (typeof workbasketDistributionTargets !== 'undefined') {
        console.log(this.distributionTargetsSelected);
        this.distributionTargetsSelectedResource = { ...workbasketDistributionTargets };
        this.distributionTargetsSelected = this.distributionTargetsSelectedResource.distributionTargets;
        this.distributionTargetsSelectedClone = { ...this.distributionTargetsSelected };
        TaskanaQueryParameters.page = 1;
        this.calculateNumberItemsList();
        this.getWorkbaskets();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.action) {
      this.setBadge();
    }
  }

  onScroll(side: Side) {
    if (side === this.side.LEFT && this.page.totalPages > TaskanaQueryParameters.page) {
      this.loadingItems = true;
      this.getNextPage(side);
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

  displayDistributionTargetsPicker() {
    const dialogRef = this.matDialog.open(WorkbasketDistributionTargetsListDialogComponent, {});

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
    });
  }

  init() {
    this.onRequest();
    if (!this.workbasket._links.distributionTargets) {
      return;
    }

    this.store.dispatch(new GetWorkbasketDistributionTargets(this.workbasket._links.distributionTargets.href));
    this.savingWorkbaskets
      .triggeredDistributionTargetsSaving()
      .pipe(takeUntil(this.destroy$))
      .subscribe((savingInformation: SavingInformation) => {
        if (this.action === ACTION.COPY) {
          this.distributionTargetsSelectedResource._links.self.href = savingInformation.url;
          this.onSave();
        }
      });

    this.orientationService
      .getOrientation()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calculateNumberItemsList();
        this.getWorkbaskets();
      });
  }

  getWorkbaskets(side?: Side) {
    if (this.distributionTargetsSelected && !this.initialized) {
      this.initialized = true;
      TaskanaQueryParameters.pageSize = this.cards + this.distributionTargetsSelected.length;
    }

    // TODO: Implement this into NGXS
    this.workbasketService
      .getWorkBasketsSummary(true)
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        if (TaskanaQueryParameters.page === 1) {
          this.distributionTargetsLeft = [];
          this.page = distributionTargetsAvailable.page;
        }
        if (side === this.side.LEFT) {
          this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
        } else if (side === this.side.RIGHT) {
          this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
        } else {
          this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
          this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
          this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable.workbaskets);
        }
        this.onRequest(true);
      });
  }

  performFilter(dualListFilter: any) {
    this.fillDistributionTargets(dualListFilter.side, undefined);
    this.onRequest(false, dualListFilter.side);
    this.store
      .dispatch(
        new GetWorkbasketsSummary(
          true,
          '',
          '',
          '',
          dualListFilter.filterBy.filterParams.name,
          dualListFilter.filterBy.filterParams.description,
          '',
          dualListFilter.filterBy.filterParams.owner,
          dualListFilter.filterBy.filterParams.type,
          '',
          dualListFilter.filterBy.filterParams.key,
          '',
          true
        )
      )
      .subscribe((state: WorkbasketStateModel) => {
        this.fillDistributionTargets(dualListFilter.side, state.paginatedWorkbasketsSummary.workbaskets);
        this.onRequest(true, dualListFilter.side);
      });
  }

  onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    this.store
      .dispatch(
        new UpdateWorkbasketDistributionTargets(
          this.distributionTargetsSelectedResource._links.self.href,
          this.getSeletedIds()
        )
      )
      .subscribe(
        () => {
          this.requestInProgressService.setRequestInProgress(false);
          return true;
        },
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          return false;
        }
      );
    /* TODO: OLD IMPLEMENTATION, KEPT HERE FOR REFERENCE
    this.workbasketService.updateWorkBasketsDistributionTargets(
      this.distributionTargetsSelectedResource._links.self.href, this.getSeletedIds()
    ).subscribe(response => {
      this.requestInProgressService.setRequestInProgress(false);
      this.distributionTargetsSelected = response.distributionTargets;
      this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
      this.distributionTargetsClone = Object.assign([], this.distributionTargetsLeft);
      this.notificationsService.showToast(
        NOTIFICATION_TYPES.SUCCESS_ALERT_8,
        new Map<string, string>([['workbasketName', this.workbasket.name]])
      );
      return true;
    },
    error => {
      this.notificationsService.triggerError(NOTIFICATION_TYPES.SAVE_ERR_3, error);
      this.requestInProgressService.setRequestInProgress(false);
      return false;
    });
    */
    return false;
  }

  moveDistributionTargets(side: number) {
    if (side === Side.LEFT) {
      const itemsLeft = this.distributionTargetsLeft.length;
      const itemsRight = this.distributionTargetsRight.length;
      const itemsSelected = this.getSelectedItems(this.distributionTargetsLeft);
      this.distributionTargetsSelected = [...this.distributionTargetsSelected, ...itemsSelected];
      this.distributionTargetsRight = this.distributionTargetsRight.concat(itemsSelected);
      if (
        itemsLeft - itemsSelected.length <= TaskanaQueryParameters.pageSize &&
        itemsLeft + itemsRight < this.page.totalElements
      ) {
        this.getNextPage(side);
      }
      this.unselectItems(this.distributionTargetsSelected);
    } else {
      const itemsSelected = this.getSelectedItems(this.distributionTargetsRight);
      this.distributionTargetsSelected = this.removeSelectedItems(this.distributionTargetsSelected, itemsSelected);
      this.distributionTargetsRight = this.removeSelectedItems(this.distributionTargetsRight, itemsSelected);
      this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
      this.unselectItems(itemsSelected);
    }
  }

  onClear() {
    this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
    this.distributionTargetsLeft = Object.assign([], this.distributionTargetsClone);
    this.distributionTargetsRight = Object.assign([], this.distributionTargetsSelectedClone);
    this.distributionTargetsSelected = Object.assign([], this.distributionTargetsSelectedClone);
  }

  calculateNumberItemsList() {
    /*if (this.panelBody) {
      const cardHeight = 72;
      const unusedHeight = 100;
      this.cards =
        this.orientationService.calculateNumberItemsList(
          this.panelBody.nativeElement.offsetHeight,
          cardHeight,
          unusedHeight,
          true
        ) + 1; // TODO: warum +1
    }*/
  }

  fillDistributionTargets(side: Side, workbaskets: WorkbasketSummary[]) {
    this.distributionTargetsLeft = side === Side.LEFT ? workbaskets : this.distributionTargetsLeft;
    this.distributionTargetsRight = side === Side.RIGHT ? workbaskets : this.distributionTargetsRight;
  }

  getNextPage(side: Side) {
    TaskanaQueryParameters.page += 1;
    this.getWorkbaskets(side);
  }

  setBadge() {
    if (this.action === ACTION.COPY) {
      this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
    }
  }

  getSelectedItems(originList: any): Array<any> {
    return originList.filter((item: any) => item.selected === true);
  }

  selectWorkbasket(workbasketId: string) {
    this.selectedId = workbasketId;
  }
  unselectItems(originList: any): Array<any> {
    // eslint-disable-next-line no-restricted-syntax
    for (const item of originList) {
      if (item.selected && item.selected === true) {
        item.selected = false;
      }
    }
    return originList;
  }

  removeSelectedItems(originList: any, selectedItemList) {
    for (let index = originList.length - 1; index >= 0; index--) {
      if (selectedItemList.some((itemToRemove) => originList[index].workbasketId === itemToRemove.workbasketId)) {
        originList.splice(index, 1);
      }
    }
    return originList;
  }

  onRequest(finished: boolean = false, side?: Side) {
    this.loadingItems = false;
    const inProgress = !finished;
    switch (side) {
      case Side.LEFT:
        this.requestInProgressLeft = inProgress;
        break;
      case Side.RIGHT:
        this.requestInProgressRight = inProgress;
        break;
      default:
        this.requestInProgressLeft = inProgress;
        this.requestInProgressRight = inProgress;
    }
  }

  getSeletedIds(): Array<string> {
    const distributionTargetsSelelected: Array<string> = [];
    this.distributionTargetsSelected.forEach((item) => {
      distributionTargetsSelelected.push(item.workbasketId);
    });
    return distributionTargetsSelelected;
  }

  private uncheckSelectAll(side: number) {
    if (side === Side.LEFT && this.selectAllLeft) {
      this.selectAllLeft = false;
    }
    if (side === Side.RIGHT && this.selectAllRight) {
      this.selectAllRight = false;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
