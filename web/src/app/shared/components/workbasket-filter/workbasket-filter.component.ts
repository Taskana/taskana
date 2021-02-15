import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ALL_TYPES, WorkbasketType } from '../../models/workbasket-type';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { Select, Store } from '@ngxs/store';
import { ClearFilter, SetFilter } from '../../store/filter-store/filter.actions';
import { FilterSelectors } from '../../store/filter-store/filter.selectors';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'taskana-shared-workbasket-filter',
  templateUrl: './workbasket-filter.component.html',
  styleUrls: ['./workbasket-filter.component.scss']
})
export class WorkbasketFilterComponent implements OnInit, OnDestroy {
  allTypes: Map<WorkbasketType, string> = ALL_TYPES;

  @Input() component: string;
  @Input() isExpanded: boolean;

  @Select(FilterSelectors.getAvailableDistributionTargetsFilter)
  availableDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  @Select(FilterSelectors.getSelectedDistributionTargetsFilter)
  selectedDistributionTargetsFilter$: Observable<WorkbasketQueryFilterParameter>;

  @Select(FilterSelectors.getWorkbasketListFilter)
  workbasketListFilter$: Observable<WorkbasketQueryFilterParameter>;

  destroy$ = new Subject<void>();

  filter: WorkbasketQueryFilterParameter;

  constructor(private store: Store) {}

  ngOnInit(): void {
    if (this.component === 'availableDistributionTargets') {
      this.availableDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
        this.setFilter(filter);
      });
    } else if (this.component === 'selectedDistributionTargets') {
      this.selectedDistributionTargetsFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
        this.setFilter(filter);
      });
    } else if (this.component === 'workbasketList') {
      this.workbasketListFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
        this.setFilter(filter);
      });
    }
  }

  setFilter(filter: WorkbasketQueryFilterParameter) {
    this.filter = {
      'description-like': [...filter['description-like']],
      'key-like': [...filter['key-like']],
      'name-like': [...filter['name-like']],
      'owner-like': [...filter['owner-like']],
      type: [...filter['type']]
    };
  }

  clear() {
    this.store.dispatch(new ClearFilter(this.component));
  }

  selectType(type: WorkbasketType) {
    this.filter.type = type ? [type] : [];
  }

  search() {
    this.store.dispatch(new SetFilter(this.filter, this.component));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
