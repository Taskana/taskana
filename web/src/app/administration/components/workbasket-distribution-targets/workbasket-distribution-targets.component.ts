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
  GetAvailableDistributionTargets,
  GetWorkbasketDistributionTargets,
  GetWorkbasketsSummary,
  UpdateWorkbasketDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { WorkbasketStateModel } from '../../../shared/store/workbasket-store/workbasket.state';
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
export class WorkbasketDistributionTargetsComponent implements OnInit, OnDestroy {
  @Input()
  workbasket: Workbasket;

  @Input()
  action: ACTION;

  badgeMessage = '';
  selectedId = '';
  toolbarState = false;
  sideBySide = false;

  distributionTargetsSelectedResource: WorkbasketDistributionTargets;
  distributionTargetsLeft: Array<WorkbasketSummary> = [];
  distributionTargetsRight: Array<WorkbasketSummary> = [];
  distributionTargetsSelected: Array<WorkbasketSummary>;
  distributionTargetsClone: Array<WorkbasketSummary>;
  distributionTargetsSelectedClone: Array<WorkbasketSummary>;
  availableDistributionTargets: WorkbasketSummary[] = [];
  displayingDistributionTargetsPicker = false;

  requestInProgressLeft = false;
  requestInProgressRight = false;
  loadingItems = false;
  side = Side;
  private initialized = false;
  page: Page;
  cards: number;
  selectAllLeft = false;
  selectAllRight = false;

  @Select(WorkbasketSelectors.workbasketDistributionTargets)
  workbasketDistributionTargets$: Observable<WorkbasketDistributionTargets>;

  @Select(WorkbasketSelectors.availableDistributionTargets)
  availableDistributionTargets$: Observable<WorkbasketSummary[]>;

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
    if (!this.workbasket._links.distributionTargets) {
      return;
    }
    this.store.dispatch(new GetWorkbasketDistributionTargets(this.workbasket._links.distributionTargets.href));
    this.store.dispatch(new GetAvailableDistributionTargets());

    this.availableDistributionTargets$.pipe(takeUntil(this.destroy$)).subscribe((availableDistributionTargets) => {
      this.availableDistributionTargets = availableDistributionTargets;
    });

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
        //this.getWorkbaskets();
      });

    this.workbasketDistributionTargets$.subscribe((workbasketDistributionTargets) => {
      if (typeof workbasketDistributionTargets !== 'undefined') {
        this.distributionTargetsSelectedResource = { ...workbasketDistributionTargets };
        this.distributionTargetsSelected = this.distributionTargetsSelectedResource.distributionTargets;
        this.distributionTargetsSelectedClone = { ...this.distributionTargetsSelected };
        TaskanaQueryParameters.page = 1;
        this.calculateNumberItemsList();
        this.getWorkbaskets();
        // this.store.dispatch(new GetAvailableDistributionTargets());
      }
    });
  }

  onScroll() {
    if (this.page.totalPages > TaskanaQueryParameters.page) {
      this.loadingItems = true;
      this.getNextPage();
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

  displayDistributionTargetsPicker() {
    this.displayingDistributionTargetsPicker = true;
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
      });
  }

  getNextPage(side?: Side) {
    TaskanaQueryParameters.page += 1;
    this.getWorkbaskets(side);
  }

  performFilter(dualListFilter: any) {
    this.workbasketService
      .getWorkBasketsSummary(
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
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        console.log(distributionTargetsAvailable);
        this.fillDistributionTargets(dualListFilter.side, []);

        if (TaskanaQueryParameters.page === 1) {
          this.distributionTargetsLeft = [];
          this.page = distributionTargetsAvailable.page;
        }
        if (dualListFilter.side === this.side.LEFT) {
          this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
        } else if (dualListFilter.side === this.side.RIGHT) {
          this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
        } else {
          this.distributionTargetsLeft.push(...distributionTargetsAvailable.workbaskets);
          this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable.workbaskets);
          this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable.workbaskets);
        }
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
    /*
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

  calculateNumberItemsList() {}

  fillDistributionTargets(side: Side, workbaskets: WorkbasketSummary[]) {
    this.distributionTargetsLeft = side === Side.LEFT ? workbaskets : this.distributionTargetsLeft;
    this.distributionTargetsRight = side === Side.RIGHT ? workbaskets : this.distributionTargetsRight;
  }

  getSelectedItems(originList: any): Array<any> {
    return originList.filter((item: any) => item.selected === true);
  }

  selectWorkbasket(workbasketId: string) {
    this.selectedId = workbasketId;
  }

  unselectItems(originList: any): Array<any> {
    for (const item of originList) {
      if (item.selected && item.selected === true) {
        item.selected = false;
      }
    }
    return originList;
  }

  removeSelectedItems(originList: any, selectedItemList) {
    const copyList = [...originList];
    for (let index = originList.length - 1; index >= 0; index--) {
      if (selectedItemList.some((itemToRemove) => originList[index].workbasketId === itemToRemove.workbasketId)) {
        copyList.splice(index, 1);
      }
    }
    return copyList;
  }

  getSeletedIds(): Array<string> {
    const distributionTargetsSelelected: Array<string> = [];
    this.distributionTargetsSelected.forEach((item) => {
      distributionTargetsSelelected.push(item.workbasketId);
    });
    return distributionTargetsSelelected;
  }

  toggleSideBySideView() {
    this.sideBySide = !this.sideBySide;
    if (!this.displayingDistributionTargetsPicker) {
      this.displayingDistributionTargetsPicker = !this.displayingDistributionTargetsPicker;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
