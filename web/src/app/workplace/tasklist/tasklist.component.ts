import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from 'app/workplace/services/task.service';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit, OnDestroy {

  private columnForOrdering: string;
  private taskChangeSubscription: Subscription;
  private taskDeletedSubscription: Subscription;

  selectedId = '';
  @Input() tasks: Task[];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private taskService: TaskService) {
    this.columnForOrdering = 'id';  // default: order tasks by id
    this.taskChangeSubscription = this.taskService.taskChangedStream.subscribe(task => {
      for (let i = 0; i < this.tasks.length; i++) {
        if (this.tasks[i].taskId === task.taskId) {
          this.tasks[i] = task;
        }
      }
    });
    this.taskDeletedSubscription = this.taskService.taskDeletedStream.subscribe(task => {
      for (let i = 0; i < this.tasks.length; i++) {
        if (this.tasks[i].taskId === task.taskId) {
          this.tasks.splice(i, 1);
        }
      }
    })
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

  ngOnDestroy(): void {
    this.taskChangeSubscription.unsubscribe();
    this.taskDeletedSubscription.unsubscribe();
  }
}
