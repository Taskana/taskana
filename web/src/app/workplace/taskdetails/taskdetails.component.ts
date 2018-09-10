import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TaskService } from 'app/workplace/services/task.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';

import { Task } from 'app/workplace/models/task';
import { ErrorModel } from '../../models/modal-error';
import { ErrorModalService } from '../../services/errorModal/error-modal.service';
import { RequestInProgressService } from '../../services/requestInProgress/request-in-progress.service';
import { AlertService } from '../../services/alert/alert.service';
import { AlertModel, AlertType } from '../../models/alert';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit, OnDestroy {
  task: Task = null;
  taskClone: Task = null;
  requestInProgress = false;
  tabSelected = 'general';

  private routeSubscription: Subscription;


  constructor(private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router,
    private removeConfirmationService: RemoveConfirmationService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService,
    private errorModalService: ErrorModalService) {
  }

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe(params => {
      const id = params['id'];
      this.getTask(id);
    });
  }

  resetTask(): void {
    this.task = { ...this.taskClone };
    this.task.customAttributes = this.taskClone.customAttributes.slice(0);
    this.task.callbackInfo = this.taskClone.callbackInfo.slice(0);
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'));
  }

  getTask(id: string): void {
    this.requestInProgress = true;
    this.taskService.getTask(id).subscribe(task => {
      this.requestInProgress = false;
      this.task = task;
      this.cloneTask();
      this.taskService.selectTask(task);
    }, err => {
      this.errorModalService.triggerError(
        new ErrorModel('An error occurred while fetching the task', err));
    });
  }

  updateTask() {
    this.requestInProgressService.setRequestInProgress(true);
    this.taskService.updateTask(this.task).subscribe(task => {
      this.requestInProgressService.setRequestInProgress(false);
      this.task = task;
      this.cloneTask();
      this.taskService.publishUpdatedTask(task);
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Update successful!'))
    }, err => {this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'Update not successful!'))});
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

  selectTab(tab: string): void {
    this.tabSelected = tab;
  }

  backClicked(): void {
    this.task = undefined;
    this.taskService.selectTask(this.task);
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }

  private cloneTask() {
    this.taskClone = { ...this.task };
    this.taskClone.customAttributes = this.task.customAttributes.slice(0);
    this.taskClone.callbackInfo = this.task.callbackInfo.slice(0);
  }
}
