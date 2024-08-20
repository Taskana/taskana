import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Navigate } from '@ngxs/router-plugin';
import { Store } from '@ngxs/store';
import { Task } from '@task/models/task';
import { GetTasks, GetTask } from '@task/store/task.actions';
import { TaskSelector } from '@task/store/task.selector';

@Injectable({
  providedIn: 'root'
})
export class TaskFacadeService {
  constructor(private store: Store) {}

  selectedTask(): Task | null {
    return this.store.selectSnapshot(TaskSelector.selectedTask);
  }

  getTasks(): void {
    this.store.dispatch(new GetTasks());
  }

  // distinguish between select a task (what happens to UI?) and the action of getting information of a task from a taskID
  selectTask(taskId: string): void {
    this.store.dispatch(new Navigate([`/kadai/workplace/tasks/taskdetail/${taskId}`]));
  }

  getTask(taskId: string): void {
    this.store.dispatch(new GetTask(taskId));
  }

  /**
   * @TODO Discuss potential method "deselectTask" if feature is present
   */
}
