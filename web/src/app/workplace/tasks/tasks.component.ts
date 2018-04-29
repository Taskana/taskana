import {Component, Input} from '@angular/core';
import {Task} from '../models/task';

@Component({
  selector: 'taskana-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss']
})
export class TasksComponent {

  @Input()
  tasks: Task[];

  @Input()
  task: Task;

  loadTasks(tasks: Task[]) {
    this.tasks = tasks;
  }

  selectTask(task: Task) {
    this.task = task;
  }

}
