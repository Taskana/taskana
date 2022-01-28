import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { isEqual } from 'lodash';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { expandDown } from 'app/shared/animations/expand.animation';
import { MatSelectionList } from '@angular/material/list';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import { Side } from '../../models/workbasket-distribution-enums';
import { Select, Store } from '@ngxs/store';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { filter, map, pairwise, take, takeUntil, throttleTime } from 'rxjs/operators';
import {
  FetchAvailableDistributionTargets,
  FetchWorkbasketDistributionTargets,
  TransferDistributionTargets
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { Observable, Subject } from 'rxjs';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { FilterSelectors } from '../../../shared/store/filter-store/filter.selectors';
import { WorkbasketDistributionTarget } from '../../../shared/models/workbasket-distribution-target';

@Component({
  selector: 'taskana-administration-workbasket-distribution-targets-list',
  templateUrl: './workbasket-distribution-targets-list.component.html',
  styleUrls: ['./workbasket-distribution-targets-list.component.scss'],
  animations: [expandDown]
})
export class WorkbasketDistributionTargetsListComponent
  implements AfterContentChecked, OnChanges, OnInit, AfterViewInit
{
  @Input() side: Side;
  @Input() header: string;
  allSelected;
  @Input() component;
  @Input() transferDistributionTargetObservable: Observable<Side>;

  @Select(WorkbasketSelectors.workbasketDistributionTargets)
  workbasketDistributionTargets$: Observable<WorkbasketSummary[]>;

  @Select(WorkbasketSelectors.availableDistributionTargets)
  availableDistributionTargets$: Observable<WorkbasketSummary[]>;

  @Select(FilterSelectors.getAvailableDistributionTargetsFilter)
  availableDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  @Select(FilterSelectors.getSelectedDistributionTargetsFilter)
  selectedDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  toolbarState = false;

  distributionTargets: WorkbasketDistributionTarget[];

  @ViewChild('workbasket') distributionTargetsList: MatSelectionList;
  @ViewChild('scroller') workbasketList: CdkVirtualScrollViewport;
  private destroy$ = new Subject<void>();
  private filter: WorkbasketQueryFilterParameter;
  private allSelectedDiff = 0;

  constructor(private changeDetector: ChangeDetectorRef, private store: Store) {}

  ngOnInit(): void {
    if (this.side === Side.AVAILABLE) {
      this.availableDistributionTargets$.pipe(takeUntil(this.destroy$)).subscribe((wbs) => {
        this.distributionTargets = wbs.map((wb) => {
          return { ...wb, selected: this.allSelected };
        });
      });
      this.availableDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
        this.filter = filter;
        this.store.dispatch(new FetchAvailableDistributionTargets(true, this.filter));
        this.selectAll(false);
      });
    } else {
      this.workbasketDistributionTargets$.pipe().subscribe((wbs) => {
        this.distributionTargets = wbs.map((wb) => {
          return { ...wb };
        });
      });
      this.selectedDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
        if (isEqual(this.filter, filter)) return;
        this.filter = filter;
        this.store
          .dispatch(new FetchWorkbasketDistributionTargets(true))
          .pipe(take(1))
          .subscribe(() => this.applyFilter());
        this.selectAll(false);
      });
    }
    this.transferDistributionTargetObservable.subscribe((targetSide) => {
      if (targetSide !== this.side) this.transferDistributionTargets(targetSide);
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetector.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (typeof changes.allSelected?.currentValue !== 'undefined') {
      this.selectAll(changes.allSelected.currentValue);
    }
  }

  ngAfterViewInit() {
    this.workbasketList
      .elementScrolled()
      .pipe(
        map(() => this.workbasketList.measureScrollOffset('bottom')),
        pairwise(),
        filter(([y1, y2]) => y2 < y1 && y2 < 270),
        throttleTime(200)
      )
      .subscribe(() => {
        if (this.side === Side.AVAILABLE) {
          this.store.dispatch(new FetchAvailableDistributionTargets(false, this.filter));
        } else {
          this.store.dispatch(new FetchWorkbasketDistributionTargets(false, this.filter));
        }
      });
  }

  selectAll(selected: boolean) {
    if (typeof this.distributionTargetsList !== 'undefined') {
      this.allSelected = selected;
      this.distributionTargets.map((wb) => (wb.selected = selected));
      if (selected) this.allSelectedDiff = this.distributionTargets.length;
      else this.allSelectedDiff = 0;
    }
  }

  transferDistributionTargets(targetSide: Side) {
    let selectedWBs = this.distributionTargets.filter((item: any) => item.selected === true);
    this.distributionTargets.forEach((wb) => (wb.selected = false));
    this.store
      .dispatch(new TransferDistributionTargets(targetSide, selectedWBs))
      .pipe(take(1))
      .subscribe(() => {
        if (this.distributionTargets.length === 0) {
          const desiredAction =
            targetSide === Side.SELECTED
              ? new FetchAvailableDistributionTargets(false, this.filter)
              : new FetchWorkbasketDistributionTargets(false, this.filter);
          this.store.dispatch(desiredAction);
        }
      });
  }

  changeToolbarState(state: boolean) {
    this.toolbarState = state;
  }

  updateSelectAll(selected: boolean) {
    if (selected) this.allSelectedDiff++;
    else this.allSelectedDiff--;
    this.allSelected = this.allSelectedDiff === this.distributionTargets.length;
    return true;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private applyFilter() {
    function filterExact(target: WorkbasketDistributionTarget, filterStrings: string[], attribute: string) {
      if (!!filterStrings && filterStrings?.length !== 0) {
        return filterStrings.map((str) => str.toLowerCase()).includes(target[attribute].toLowerCase());
      }
      return true;
    }

    function filterLike(target: WorkbasketDistributionTarget, filterStrings: string[], attribute: string) {
      if (!!filterStrings && filterStrings?.length !== 0) {
        let ret = true;
        filterStrings.forEach((filterElement) => {
          ret = ret && target[attribute].toLowerCase().includes(filterElement.toLowerCase());
        });
        return ret;
      }
      return true;
    }

    this.distributionTargets = this.distributionTargets?.filter((target) => {
      let matches = true;
      matches = matches && filterExact(target, this.filter.name, 'name');
      matches = matches && filterExact(target, this.filter.key, 'key');
      matches = matches && filterExact(target, this.filter.owner, 'owner');
      matches = matches && filterExact(target, this.filter.domain, 'domain');
      matches = matches && filterExact(target, this.filter.type, 'type');
      matches = matches && filterLike(target, this.filter['owner-like'], 'owner');
      matches = matches && filterLike(target, this.filter['name-like'], 'name');
      matches = matches && filterLike(target, this.filter['key-like'], 'key');
      matches = matches && filterLike(target, this.filter['description-like'], 'description');
      return matches;
    });
  }
}
