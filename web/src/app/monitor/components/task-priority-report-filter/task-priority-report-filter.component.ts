import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { Select } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { SettingsSelectors } from '../../../shared/store/settings-store/settings.selectors';
import { Settings } from '../../../settings/models/settings';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'kadai-monitor-task-priority-report-filter',
  templateUrl: './task-priority-report-filter.component.html',
  styleUrls: ['./task-priority-report-filter.component.scss']
})
export class TaskPriorityReportFilterComponent implements OnInit, OnDestroy {
  isPanelOpen = false;
  filters: {}[];
  keys: string[];
  activeFilters = [];
  filtersAreSpecified: boolean = true;
  destroy$ = new Subject<void>();

  @Output() applyFilter = new EventEmitter<Object>();

  @Select(SettingsSelectors.getSettings)
  settings$: Observable<Settings>;

  ngOnInit() {
    this.settings$.pipe(takeUntil(this.destroy$)).subscribe((settings) => {
      this.filtersAreSpecified = settings['filter'] && settings['filter'] !== '';
      if (this.filtersAreSpecified) {
        this.filters = JSON.parse(settings['filter']);
        this.keys = Object.keys(this.filters);
      }
    });
  }

  emitFilter(isEnabled: boolean, key: string) {
    this.activeFilters = isEnabled
      ? [...this.activeFilters, key]
      : this.activeFilters.filter((element) => element !== key);

    this.applyFilter.emit(this.buildQuery());
  }

  buildQuery(): {} {
    let filterQuery = {};
    this.activeFilters.forEach((activeFilter) => {
      const filter = this.filters[activeFilter];
      const keys = Object.keys(filter);
      keys.forEach((key) => {
        const newValue = filter[key];
        filterQuery[key] = filterQuery[key] ? [...filterQuery[key], ...newValue] : newValue;
      });
    });
    return filterQuery;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
