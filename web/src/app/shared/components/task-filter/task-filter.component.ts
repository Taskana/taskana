import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ALL_STATES, TaskState } from '../../models/task-state';
import { TaskQueryFilterParameter } from '../../models/task-query-filter-parameter';

@Component({
  selector: 'taskana-shared-task-filter',
  templateUrl: './task-filter.component.html',
  styleUrls: ['./task-filter.component.scss']
})
export class TaskFilterComponent implements OnInit {
  filter: TaskQueryFilterParameter;

  @Output() performFilter = new EventEmitter<TaskQueryFilterParameter>();

  allStates: Map<TaskState, string> = ALL_STATES;

  ngOnInit(): void {
    this.clear();
  }

  selectState(state: TaskState) {
    this.filter.state = state ? [state] : [];
  }

  search() {
    this.performFilter.emit(this.filter);
  }

  clear() {
    this.filter = {
      priority: [],
      'name-like': [],
      'owner-like': []
    };
  }
}
