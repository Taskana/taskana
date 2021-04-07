import { Component, Input, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Actions, ofActionCompleted, Select, Store } from '@ngxs/store';
import { filter, take, takeUntil } from 'rxjs/operators';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import {
  GetAvailableDistributionTargets,
  GetWorkbasketDistributionTargets,
  SaveNewWorkbasket,
  UpdateWorkbasket,
  UpdateWorkbasketDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { ButtonAction } from '../../models/button-action';
import { Pair } from '../../../shared/models/pair';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { FilterSelectors } from '../../../shared/store/filter-store/filter.selectors';
import { SetWorkbasketFilter } from '../../../shared/store/filter-store/filter.actions';

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

  toolbarState = false;
  sideBySide = true;
  displayingDistributionTargetsPicker = true;

  side = Side;
  selectAllLeft = false;
  selectAllRight = false;

  availableDistributionTargets: WorkbasketSummary[] = [];
  availableDistributionTargetsUndoClone: WorkbasketSummary[];
  availableDistributionTargetsFilterClone: WorkbasketSummary[] = [];
  availableDistributionTargetsFilter: WorkbasketQueryFilterParameter;

  selectedDistributionTargets: WorkbasketSummary[];
  selectedDistributionTargetsUndoClone: WorkbasketSummary[];
  selectedDistributionTargetsFilterClone: WorkbasketSummary[] = [];
  selectedDistributionTargetsResource: WorkbasketDistributionTargets;
  selectedDistributionTargetsFilter: WorkbasketQueryFilterParameter;

  @Select(WorkbasketSelectors.workbasketDistributionTargets)
  workbasketDistributionTargets$: Observable<WorkbasketDistributionTargets>;

  @Select(WorkbasketSelectors.availableDistributionTargets)
  availableDistributionTargets$: Observable<WorkbasketSummary[]>;

  @Select(WorkbasketSelectors.buttonAction)
  buttonAction$: Observable<ButtonAction>;

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  @Select(FilterSelectors.getAvailableDistributionTargetsFilter)
  availableDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  @Select(FilterSelectors.getSelectedDistributionTargetsFilter)
  selectedDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  destroy$ = new Subject<void>();

  constructor(
    private workbasketService: WorkbasketService,
    private notificationsService: NotificationService,
    private store: Store,
    private ngxsActions$: Actions
  ) {}

  /**
   * Rework with modification based on old components,
   * would be ideal to completely redo whole components using drag and drop angular components and clearer logics
   */
  ngOnInit() {
    this.selectedWorkbasket$
      .pipe(
        filter((selectedWorkbasket) => typeof selectedWorkbasket !== 'undefined'),
        takeUntil(this.destroy$)
      )
      .subscribe((selectedWorkbasket) => {
        this.workbasket = selectedWorkbasket;
      });

    if (this.workbasket?.workbasketId) {
      this.store.dispatch(new GetWorkbasketDistributionTargets(this.workbasket._links.distributionTargets.href));
      this.store.dispatch(new GetAvailableDistributionTargets());
    }

    this.workbasketDistributionTargets$.pipe(takeUntil(this.destroy$)).subscribe((workbasketDistributionTargets) => {
      if (typeof workbasketDistributionTargets !== 'undefined') {
        this.selectedDistributionTargetsResource = { ...workbasketDistributionTargets };

        this.selectedDistributionTargets = [];
        workbasketDistributionTargets.distributionTargets.forEach((distributionTarget) => {
          const target = {};
          Object.keys(distributionTarget).forEach((key) => {
            target[key] = distributionTarget[key];
          });
          this.selectedDistributionTargets.push(target);
        });

        this.selectedDistributionTargetsFilterClone = [...this.selectedDistributionTargets];
        this.selectedDistributionTargetsUndoClone = [...this.selectedDistributionTargets];

        this.getAvailableDistributionTargets();
      }
    });

    this.availableDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
      this.availableDistributionTargetsFilter = filter;
      this.performFilter({ left: 0, right: filter });
    });

    this.selectedDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
      this.selectedDistributionTargetsFilter = filter;
      this.performFilter({ left: 1, right: filter });
    });

    // saving workbasket distributions targets when existing workbasket was modified
    this.ngxsActions$.pipe(ofActionCompleted(UpdateWorkbasket), takeUntil(this.destroy$)).subscribe(() => {
      this.onSave();
    });

    // saving workbasket distributions targets when workbasket was copied or created
    this.ngxsActions$.pipe(ofActionCompleted(SaveNewWorkbasket), takeUntil(this.destroy$)).subscribe(() => {
      this.selectedWorkbasket$.pipe(take(1)).subscribe((workbasket) => {
        this.selectedDistributionTargetsResource._links = {
          self: { href: workbasket._links.distributionTargets.href }
        };
        this.onSave();
      });
    });

    this.buttonAction$
      .pipe(takeUntil(this.destroy$))
      .pipe(filter((buttonAction) => typeof buttonAction !== 'undefined'))
      .subscribe((button) => {
        switch (button) {
          case ButtonAction.UNDO:
            this.onClear();
            break;
          default:
            break;
        }
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.workbasket.currentValue !== changes.workbasket.previousValue) {
      this.getAvailableDistributionTargets();
    }
  }

  filterWorkbasketsByWorkbasketIDs(workbaskets: WorkbasketSummary[], IDs: WorkbasketSummary[]): WorkbasketSummary[] {
    const workbasketIds: string[] = IDs.map((workbasket) => workbasket.workbasketId);
    return workbaskets.filter((workbasket) => !workbasketIds.includes(workbasket.workbasketId));
  }

  getAvailableDistributionTargets() {
    this.availableDistributionTargets$
      .pipe(
        take(1),
        filter((availableDistributionTargets) => typeof availableDistributionTargets !== 'undefined')
      )
      .subscribe((availableDistributionTargets) => {
        this.availableDistributionTargets = availableDistributionTargets.map((wb) => ({ ...wb }));

        if (this.selectedDistributionTargets && this.selectedDistributionTargets.length !== 0) {
          this.availableDistributionTargets = this.filterWorkbasketsByWorkbasketIDs(
            this.availableDistributionTargets,
            this.selectedDistributionTargets
          );
        }

        this.availableDistributionTargetsUndoClone = [...this.availableDistributionTargets];
        this.availableDistributionTargetsFilterClone = [...this.availableDistributionTargets];
      });
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

  toggleDistributionTargetsPicker() {
    this.displayingDistributionTargetsPicker = !this.displayingDistributionTargetsPicker;
  }

  // TODO:
  //  an own filter would save a lot of work here because the workbasketService filter
  //  returns all available distribution targets and must be filtered again
  performFilter({ left: side, right: filter }: Pair<Side, WorkbasketQueryFilterParameter>) {
    this.workbasketService
      .getWorkBasketsSummary(true, filter)
      .pipe(takeUntil(this.destroy$))
      .subscribe((distributionTargetsAvailable: WorkbasketSummaryRepresentation) => {
        const isFilterEmpty =
          filter['name-like'].length === 0 &&
          filter['key-like'].length === 0 &&
          filter['description-like'].length === 0 &&
          filter['owner-like'].length === 0 &&
          filter['type'].length === 0;

        // filter available side
        if (side === Side.AVAILABLE) {
          if (isFilterEmpty) {
            this.availableDistributionTargets = this.availableDistributionTargetsFilterClone;
          } else {
            this.availableDistributionTargets = this.filterWorkbasketsByWorkbasketIDs(
              distributionTargetsAvailable.workbaskets,
              this.selectedDistributionTargetsFilterClone
            );
          }
        }
        // filter selected side
        else if (side === Side.SELECTED) {
          if (isFilterEmpty) {
            this.selectedDistributionTargets = this.selectedDistributionTargetsFilterClone;
          } else {
            const ids = distributionTargetsAvailable.workbaskets.map((workbasket) => workbasket.workbasketId);
            this.selectedDistributionTargets = this.selectedDistributionTargetsFilterClone.filter((workbasket) =>
              ids.includes(workbasket.workbasketId)
            );
          }
        }
      });
  }

  onSave() {
    this.store.dispatch(
      new UpdateWorkbasketDistributionTargets(
        this.selectedDistributionTargetsResource._links.self.href,
        this.getSelectedIds()
      )
    );
    return false;
  }

  moveDistributionTargets(side: number) {
    // get all workbaskets without applied filter and without overwriting the selected property
    this.selectedDistributionTargets = this.selectedDistributionTargets.concat(
      this.filterWorkbasketsByWorkbasketIDs(
        this.selectedDistributionTargetsFilterClone,
        this.selectedDistributionTargets
      )
    );
    this.availableDistributionTargets = this.availableDistributionTargets.concat(
      this.filterWorkbasketsByWorkbasketIDs(
        this.availableDistributionTargetsFilterClone,
        this.availableDistributionTargets
      )
    );

    if (side === Side.AVAILABLE) {
      // moving available items to selected side
      const itemsSelected = this.getSelectedItems(this.availableDistributionTargets);
      this.selectedDistributionTargets = this.selectedDistributionTargets.concat(itemsSelected);
      this.availableDistributionTargets = this.removeSelectedItems(this.availableDistributionTargets, itemsSelected);
      this.unselectItems(itemsSelected);
    } else {
      // moving selected items to available side
      const itemsSelected = this.getSelectedItems(this.selectedDistributionTargets);
      this.selectedDistributionTargets = this.removeSelectedItems(this.selectedDistributionTargets, itemsSelected);
      this.availableDistributionTargets = this.availableDistributionTargets.concat(itemsSelected);
      this.unselectItems(itemsSelected);
    }
    this.selectedDistributionTargetsFilterClone = this.selectedDistributionTargets;
    this.availableDistributionTargetsFilterClone = this.availableDistributionTargets;
    this.selectAllRight = true;
    this.selectAllLeft = true;
    this.store.dispatch(new SetWorkbasketFilter(this.selectedDistributionTargetsFilter, 'selectedDistributionTargets'));
    this.store.dispatch(
      new SetWorkbasketFilter(this.availableDistributionTargetsFilter, 'availableDistributionTargets')
    );
  }

  onClear() {
    this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
    this.availableDistributionTargets = Object.assign([], this.availableDistributionTargetsUndoClone);
    this.availableDistributionTargetsFilterClone = Object.assign([], this.availableDistributionTargetsUndoClone);
    this.selectedDistributionTargets = Object.assign([], this.selectedDistributionTargetsUndoClone);
    this.selectedDistributionTargetsFilterClone = Object.assign([], this.selectedDistributionTargetsUndoClone);
  }

  getSelectedItems(originList: any): any[] {
    return originList.filter((item: any) => item.selected === true);
  }

  getSelectedIds(): string[] {
    return this.selectedDistributionTargetsFilterClone.map((distributionTarget) => distributionTarget.workbasketId);
  }

  unselectItems(originList: any[]): any[] {
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

  toggleSideBySideView() {
    this.sideBySide = !this.sideBySide;
    this.displayingDistributionTargetsPicker = true; //always display picker when toggle from side-by-side to single
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
