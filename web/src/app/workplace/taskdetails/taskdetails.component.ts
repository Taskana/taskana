import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TaskService } from 'app/workplace/services/task.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';

import { Task } from 'app/workplace/models/task';

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
    private removeConfirmationService: RemoveConfirmationService) {
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
    this.router.navigate([{ outlets: { detail: `task/${taskId}` } }], { relativeTo: this.route.parent });
  }

  workOnTaskDisabled(): boolean {
    return this.task ? this.task.state === 'COMPLETED' : false;
  }

  deleteTask(): void {
    this.removeConfirmationService.setRemoveConfirmation(this.deleteTaskConfirmation.bind(this),
      `You are going to delete Task: ${this.task.taskId}. Can you confirm this action?`);
  }

  deleteTaskConfirmation(): void {
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
