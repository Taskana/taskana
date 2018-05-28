import {Component, OnDestroy, OnInit} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from 'app/workplace/services/task.service';
import {Subscription} from 'rxjs/Subscription';
import {Location} from '@angular/common';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit, OnDestroy {
  task: Task = null;
  requestInProgress = false;

  private routeSubscription: Subscription;

  constructor(private route: ActivatedRoute,
              private taskService: TaskService,
              private router: Router,
              private location: Location) {
  }

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe(params => {
      const id = params['id'];
      this.getTask(id);
    });
  }

  getTask(id: string): void {
    this.requestInProgress = true;
    this.taskService.getTask(id).subscribe(task => {
      this.requestInProgress = false;
      this.task = task;
    });
  }

  updateTask() {
    this.requestInProgress = true;
    this.taskService.updateTask(this.task).subscribe(task => {
      this.requestInProgress = false;
      this.task = task;
      this.taskService.publishUpdatedTask(task);
    });
  }

  openTask(taskId: string) {
    this.router.navigate([{outlets: {detail: `task/${taskId}`}}], {relativeTo: this.route.parent});
  }

  workOnTaskDisabled(): boolean {
    return this.task ? this.task.state === 'COMPLETED' : false;
  }

  deleteTask(): void {
    this.taskService.deleteTask(this.task).subscribe();
    this.taskService.publishDeletedTask(this.task);
    this.task = null;
    this.router.navigate([`/workplace/tasks`]);
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }
}
