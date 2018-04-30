import {Component, Input, OnInit} from '@angular/core';
import {Task} from '../models/task';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit {

  private columnForOrdering: string;

  selectedId = '';
  @Input() tasks: Task[];

  constructor(private router: Router,
              private route: ActivatedRoute) {
    this.columnForOrdering = 'id';  // default: order tasks by id
  }

  ngOnInit() {
  }

  orderTasks(column: string) {
    this.columnForOrdering = column;
  }

  loadTasks(tasks: Task[]) {
    this.tasks = tasks;
  }

  selectTask(taskId: string) {
    this.selectedId = taskId;
    this.router.navigate([{outlets: {detail: `taskdetail/${this.selectedId}`}}], {relativeTo: this.route});
  }
}
