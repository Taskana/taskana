import { Component, OnDestroy, OnInit } from '@angular/core';
import { ALL_STATES, TaskState } from '../../models/task-state';
import { TaskQueryFilterParameter } from '../../models/task-query-filter-parameter';
import { Actions, ofActionCompleted, Store } from '@ngxs/store';
import { ClearTaskFilter, SetTaskFilter } from '../../store/filter-store/filter.actions';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'taskana-shared-task-filter',
  templateUrl: './task-filter.component.html',
  styleUrls: ['./task-filter.component.scss']
})
export class TaskFilterComponent implements OnInit, OnDestroy {
  filter: TaskQueryFilterParameter;
  destroy$ = new Subject<void>();

  allStates: Map<TaskState, string> = ALL_STATES;

  constructor(private store: Store, private ngxsActions$: Actions) {}

  ngOnInit() {
    this.clear();
    this.ngxsActions$.pipe(ofActionCompleted(ClearTaskFilter), takeUntil(this.destroy$)).subscribe(() => this.clear());
  }

  setStatus(state: TaskState) {
    this.filter.state = state ? [state] : [];
    this.updateState();
  }

  // TODO: filter tasks when pressing 'enter'
  search() {}

  updateState() {
    this.store.dispatch(new SetTaskFilter(this.filter));
  }

  clear() {
    this.filter = {
      priority: [],
      'name-like': [],
      'owner-like': []
    };
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
