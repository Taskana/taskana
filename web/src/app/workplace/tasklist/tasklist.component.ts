import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Task } from '../models/task';
import { Router, ActivatedRoute } from '@angular/router';
import { TaskService } from '../services/task.service';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit {

  private columnForOrdering: string;

  @Output()
  task = new EventEmitter<Task>();

  @Input() tasks: Task[];

  constructor(
    private taskService: TaskService,
    private router: Router,
    private route: ActivatedRoute) {
    this.columnForOrdering = 'id';  // default: order tasks by id
  }

  ngOnInit() {
  }

  selectTask(task: Task) {
    this.task.next(task);
  }

  orderTasks(column: string) {
    this.columnForOrdering = column;
  }

  openTask(id: string) {
    this.taskService.claimTask(id).subscribe();
    this.router.navigate(['tasks/', id], { relativeTo: this.route.parent });
  }
}
