import { Injectable } from '@angular/core';
import { Selector } from '@ngxs/store';
import { Task, TaskSummary } from '@task/models/task';
import { TaskState, TaskStateModel } from './task.state';

@Injectable()
export class TaskSelector {
  @Selector([TaskState])
  static tasks(state: TaskStateModel): TaskSummary[] {
    return state.pagedTask.tasks;
  }

  @Selector([TaskState])
  static selectedTask(state: TaskStateModel): Task | null {
    return state.selectedTask;
  }
}
