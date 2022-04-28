import { Injectable } from '@angular/core';
import { Action, State, StateContext } from '@ngxs/store';
import { PagedTaskSummary } from '@task/models/paged-task';
import { TaskService } from '@task/services/task.service';
import { take, tap } from 'rxjs';
import { GetTask, GetTasks } from './task.actions';
import { Task } from '@task/models/task';

export interface TaskStateModel {
  pagedTask: PagedTaskSummary | null;
  selectedTask: Task | null;
}

const defaults: TaskStateModel = {
  pagedTask: { tasks: [], page: {} },
  selectedTask: null
};

@State<TaskStateModel>({
  name: 'task',
  defaults
})
@Injectable()
export class TaskState {
  constructor(private taskService: TaskService) {}

  @Action(GetTasks)
  getTasks(ctx: StateContext<TaskStateModel>) {
    return this.taskService.getTasks().pipe(
      take(1),
      tap((pagedTask) => {
        ctx.patchState({ pagedTask });
      })
    );
  }

  @Action(GetTask)
  getTask(ctx: StateContext<TaskStateModel>, { taskId }: GetTask) {
    return this.taskService.getTask(taskId).pipe(
      take(1),
      tap((selectedTask) => {
        ctx.patchState({ selectedTask });
      })
    );
  }
}
