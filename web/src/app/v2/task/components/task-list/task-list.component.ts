import { Component, OnInit } from '@angular/core';
import { Select } from '@ngxs/store';
import { TaskSummary } from '@task/models/task';
import { TaskFacadeService } from '@task/services/task-facade.service';
import { TaskSelector } from '@task/store/task.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'kadai-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  @Select(TaskSelector.tasks)
  tasks$: Observable<TaskSummary[]>;

  @Select(TaskSelector.selectedTask) selectedTask$: Observable<Task | null>;

  constructor(private taskFacade: TaskFacadeService) {}

  ngOnInit(): void {}

  selectTask(taskId: string): void {
    if (this.isTaskSelected(taskId)) {
      /**
       * @TODO Add deselectTask to facade
       */
    } else {
      this.taskFacade.selectTask(taskId);
    }
  }

  isTaskSelected(taskId: string): boolean {
    return this.taskFacade.selectedTask()?.taskId === taskId;
  }
}
