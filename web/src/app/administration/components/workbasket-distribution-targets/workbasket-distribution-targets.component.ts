import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';
import { ACTION } from 'app/shared/models/action';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets.service';
import { Page } from 'app/shared/models/page';
import { Select, Store } from '@ngxs/store';
import { filter, takeUntil } from 'rxjs/operators';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import {
  GetAvailableDistributionTargets,
  GetWorkbasketDistributionTargets,
  UpdateWorkbasketDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { ButtonAction } from '../../models/button-action';
import { Pair } from '../../../shared/models/pair';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { QueryPagingParameter } from '../../../shared/models/query-paging-parameter';

export enum Side {
  AVAILABLE,
  SELECTED
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

  toolbarState = false;
  sideBySide = true;
  displayingDistributionTargetsPicker = true;

  distributionTargetsSelectedResource: WorkbasketDistributionTargets;
  availableDistributionTargets: Array<WorkbasketSummary> = [];
  distributionTargetsClone: Array<WorkbasketSummary>;

  distributionTargetsLeft: Array<WorkbasketSummary> = [];
  distributionTargetsSelected: Array<WorkbasketSummary>;
  distributionTargetsSelectedClone: Array<WorkbasketSummary>;

  loadingItems = false;
  side = Side;
  private initialized = false;
  currentPage: Page;
  pageParameter: QueryPagingParameter = {
    page: 1,
    'page-size': 9
  };
  cards: number;
  selectAllLeft = false;
  selectAllRight = false;

  @Select(WorkbasketSelectors.workbasketDistributionTargets)
  workbasketDistributionTargets$: Observable<WorkbasketDistributionTargets>;

  @Select(WorkbasketSelectors.availableDistributionTargets)
  availableDistributionTargets$: Observable<WorkbasketSummary[]>;

  @Select(WorkbasketSelectors.buttonAction)
  buttonAction$: Observable<ButtonAction>;

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  destroy$ = new Subject<void>();

  constructor(
    private workbasketService: WorkbasketService,
    private savingWorkbaskets: SavingWorkbasketService,
    private notificationsService: NotificationService,
    private store: Store
  ) {}

  /**
   * Rework with modification based on old components,
   * would be ideal to completely redo whole components using drag and drop angular components and clearer logics
   */
  ngOnInit() {
    this.selectedWorkbasket$
      .pipe(filter((selectedWorkbasket) => typeof selectedWorkbasket !== 'undefined'))
      .subscribe((selectedWorkbasket) => {
        this.workbasket = selectedWorkbasket;
      });
    if (Object.keys(this.workbasket).length !== 0) {
      this.store.dispatch(new GetWorkbasketDistributionTargets(this.workbasket._links.distributionTargets.href));
      this.store.dispatch(new GetAvailableDistributionTargets());
    }

    this.availableDistributionTargets$
      .pipe(takeUntil(this.destroy$))
      .pipe(filter((availableDistributionTargets) => typeof availableDistributionTargets !== 'undefined'))
      .subscribe((availableDistributionTargets) => {
        this.availableDistributionTargets = availableDistributionTargets.map((wb) => ({ ...wb }));
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

    this.workbasketDistributionTargets$.subscribe((workbasketDistributionTargets) => {
      if (typeof workbasketDistributionTargets !== 'undefined') {
        this.distributionTargetsSelectedResource = { ...workbasketDistributionTargets };
        this.distributionTargetsSelected = this.distributionTargetsSelectedResource.distributionTargets;
        this.distributionTargetsSelectedClone = { ...this.distributionTargetsSelected };
        this.pageParameter.page = 1;
        this.getWorkbaskets();
      }
    });
    this.buttonAction$
      .pipe(takeUntil(this.destroy$))
      .pipe(filter((buttonAction) => typeof buttonAction !== 'undefined'))
      .subscribe((button) => {
        switch (button) {
          case ButtonAction.SAVE:
            this.onSave();
            break;
          case ButtonAction.UNDO:
            this.onClear();
            break;
          default:
            break;
        }
      });
  }

  onScroll() {
    if (this.currentPage && this.currentPage.totalPages > this.pageParameter.page) {
      this.loadingItems = true;
      this.getNextPage();
    }
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

  toggleDistributionTargetsPicker() {
    this.displayingDistributionTargetsPicker = !this.displayingDistributionTargetsPicker;
  }

  getWorkbaskets(side?: Side) {
    if (this.distributionTargetsSelected && !this.initialized) {
      this.initialized = true;
      this.pageParameter['page-size'] = this.cards + this.distributionTargetsSelected.length;
    }

    this.workbasketService
      .getWorkBasketsSummary(true, undefined, undefined, this.pageParameter)
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        if (this.pageParameter === 1) {
          this.availableDistributionTargets = [];
          this.currentPage = distributionTargetsAvailable.page;
        }
        if (side === Side.AVAILABLE) {
          this.availableDistributionTargets.push(...distributionTargetsAvailable.workbaskets);
        } else if (side === Side.SELECTED) {
          this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable.workbaskets);
        } else {
          this.availableDistributionTargets.push(...distributionTargetsAvailable.workbaskets);
          this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable.workbaskets);
          this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable.workbaskets);
        }
      });
  }

  getNextPage(side?: Side) {
    this.pageParameter.page += 1;
    this.getWorkbaskets(side);
  }

  performFilter({ left: side, right: filter }: Pair<Side, WorkbasketQueryFilterParameter>) {
    this.workbasketService
      .getWorkBasketsSummary(true, filter)
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        this.fillDistributionTargets(side, []);

        if (this.pageParameter === 1) {
          this.availableDistributionTargets = [];
          this.currentPage = distributionTargetsAvailable.page;
        }
        if (side === Side.AVAILABLE) {
          this.availableDistributionTargets.push(...distributionTargetsAvailable.workbaskets);
        } else if (side === Side.SELECTED) {
          this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable.workbaskets);
        } else {
          this.availableDistributionTargets.push(...distributionTargetsAvailable.workbaskets);
          this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable.workbaskets);
          this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable.workbaskets);
        }
      });
  }

  onSave() {
    this.store.dispatch(
      new UpdateWorkbasketDistributionTargets(
        this.distributionTargetsSelectedResource._links.self.href,
        this.getSelectedIds()
      )
    );
    return false;
  }

  moveDistributionTargets(side: number) {
    if (side === Side.AVAILABLE) {
      const itemsLeft = this.availableDistributionTargets.length;
      const itemsRight = this.distributionTargetsLeft.length;
      const itemsSelected = this.getSelectedItems(this.availableDistributionTargets);
      this.distributionTargetsSelected = [...this.distributionTargetsSelected, ...itemsSelected];
      this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
      if (
        itemsLeft - itemsSelected.length <= this.pageParameter['page-size'] &&
        itemsLeft + itemsRight < this.currentPage.totalElements
      ) {
        this.getNextPage(side);
      }
      this.unselectItems(this.distributionTargetsSelected);
    } else {
      const itemsSelected = this.getSelectedItems(this.distributionTargetsLeft);
      this.distributionTargetsSelected = this.removeSelectedItems(this.distributionTargetsSelected, itemsSelected);
      this.distributionTargetsLeft = this.removeSelectedItems(this.distributionTargetsLeft, itemsSelected);
      this.availableDistributionTargets = this.availableDistributionTargets.concat(itemsSelected);
      this.unselectItems(itemsSelected);
    }
  }

  onClear() {
    this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
    this.availableDistributionTargets = Object.assign([], this.distributionTargetsClone);
    this.distributionTargetsLeft = Object.assign([], this.distributionTargetsSelectedClone);
    this.distributionTargetsSelected = Object.assign([], this.distributionTargetsSelectedClone);
  }

  fillDistributionTargets(side: Side, workbaskets: WorkbasketSummary[]) {
    this.availableDistributionTargets = side === Side.AVAILABLE ? workbaskets : this.availableDistributionTargets;
    this.distributionTargetsLeft = side === Side.SELECTED ? workbaskets : this.distributionTargetsLeft;
  }

  getSelectedItems(originList: any): Array<any> {
    return originList.filter((item: any) => item.selected === true);
  }

  unselectItems(originList: Array<any>): Array<any> {
    return originList
      .filter((item) => item.selected)
      .map((item) => {
        item.selected = false;
      });
  }

  removeSelectedItems(originList, selectedItemList) {
    const copyList = [...originList];
    for (let index = originList.length - 1; index >= 0; index--) {
      if (selectedItemList.some((itemToRemove) => originList[index].workbasketId === itemToRemove.workbasketId)) {
        copyList.splice(index, 1);
      }
    }
    return copyList;
  }

  getSelectedIds(): Array<string> {
    return this.distributionTargetsSelected.map((distributionTarget) => distributionTarget.workbasketId);
  }

  toggleSideBySideView() {
    this.sideBySide = !this.sideBySide;
    this.displayingDistributionTargetsPicker = true; //always display picker when toggle from side-by-side to single
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
