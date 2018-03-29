import {Component, Input} from '@angular/core';
import {Task} from '../model/task';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss']
})
export class TasksComponent {

  taskDetailEnabled: boolean;

  @Input()
  tasks: Task[];

  @Input()
  task: Task;

  loadTasks(tasks: Task[]) {
    this.tasks = tasks;
  }

  selectTask(task: Task) {
    this.taskDetailEnabled = true;
    this.task = task;
  }

}
