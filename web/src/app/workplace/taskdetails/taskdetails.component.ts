import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TaskService } from 'app/workplace/services/task.service';
import { RemoveConfirmationService } from 'app/shared/services/remove-confirmation/remove-confirmation.service';

import { Task } from 'app/workplace/models/task';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/shared/models/alert';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { ObjectReference } from 'app/workplace/models/object-reference';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { ERROR_TYPES } from '../../shared/models/errors';
import { ErrorsService } from '../../shared/services/errors/errors.service';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit, OnDestroy {
  task: Task;
  taskClone: Task;
  requestInProgress = false;
  tabSelected = 'general';
  currentWorkbasket: Workbasket;
  currentId: string;
  showDetail = false;

  private routeSubscription: Subscription;
  private workbasketSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private deleteTaskSubscription: Subscription;

  constructor(private route: ActivatedRoute,
    private taskService: TaskService,
    private workplaceService: WorkplaceService,
    private router: Router,
    private removeConfirmationService: RemoveConfirmationService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService,
    private generalModalService: GeneralModalService,
    private errorsService: ErrorsService,
    private masterAndDetailService: MasterAndDetailService) {
  }

  ngOnInit() {
    this.currentWorkbasket = this.workplaceService.currentWorkbasket;
    this.workbasketSubscription = this.workplaceService.getSelectedWorkbasket().subscribe(workbasket => {
      this.currentWorkbasket = workbasket;
    });
    this.routeSubscription = this.route.params.subscribe(params => {
      this.currentId = params.id;
      // redirect if user enters through a deep-link
      if (!this.currentWorkbasket && this.currentId === 'new-task') {
        this.router.navigate(['']);
      }
      this.getTask();
    });
    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });
  }

  resetTask(): void {
    this.task = { ...this.taskClone };
    this.task.customAttributes = this.taskClone.customAttributes.slice(0);
    this.task.callbackInfo = this.taskClone.callbackInfo.slice(0);
    this.task.primaryObjRef = { ...this.taskClone.primaryObjRef };
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'));
  }

  getTask(): void {
    this.requestInProgress = true;
    if (this.currentId === 'new-task') {
      this.requestInProgress = false;
      this.task = new Task('', new ObjectReference(), this.currentWorkbasket);
    } else {
      this.taskService.getTask(this.currentId).subscribe(task => {
        this.requestInProgress = false;
        this.task = task;
        this.cloneTask();
        this.taskService.selectTask(task);
      }, error => {
        this.errorsService.updateError(ERROR_TYPES.FETCH_ERR_7, error);
      });
    }
  }

  onSubmit() {
    this.onSave();
  }

  openTask() {
    this.router.navigate([{ outlets: { detail: `task/${this.currentId}` } }], { relativeTo: this.route.parent });
  }

  workOnTaskDisabled(): boolean {
    return this.task ? this.task.state === 'COMPLETED' : false;
  }

  deleteTask(): void {
    this.removeConfirmationService.setRemoveConfirmation(this.deleteTaskConfirmation.bind(this),
      `You are going to delete Task: ${this.currentId}. Can you confirm this action?`);
  }

  deleteTaskConfirmation(): void {
    this.deleteTaskSubscription = this.taskService.deleteTask(this.task).subscribe(() => {
      this.taskService.publishUpdatedTask();
      this.task = null;
      this.router.navigate(['taskana/workplace/tasks']);
    }, error => {
      this.errorsService.updateError(ERROR_TYPES.DELETE_ERR_2, error);
    });
  }

  selectTab(tab: string): void {
    this.tabSelected = tab;
  }

  backClicked(): void {
    delete this.task;
    this.taskService.selectTask(this.task);
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
    if (this.workbasketSubscription) {
      this.workbasketSubscription.unsubscribe();
    }
    if (this.masterAndDetailSubscription) {
      this.masterAndDetailSubscription.unsubscribe();
    }
    if (this.deleteTaskSubscription) {
      this.deleteTaskSubscription.unsubscribe();
    }
  }

  private onSave() {
    this.currentId === 'new-task' ? this.createTask() : this.updateTask();
  }

  private updateTask() {
    this.requestInProgressService.setRequestInProgress(true);
    this.taskService.updateTask(this.task).subscribe(task => {
      this.requestInProgressService.setRequestInProgress(false);
      this.task = task;
      this.cloneTask();
      this.taskService.publishUpdatedTask(task);
      // new Key ALERT_TYPES.SUCCESS_ALERT_14
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'Updating was successful.'));
    }, err => {
      this.requestInProgressService.setRequestInProgress(false);
      // new Key ALERT_TYPES.DANGER_ALERT
      this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'There was an error while updating.'));
    });
  }

  private createTask() {
    this.requestInProgressService.setRequestInProgress(true);
    this.addDateToTask();
    this.taskService.createTask(this.task).subscribe(task => {
      this.requestInProgressService.setRequestInProgress(false);
      // new Key ALERT_TYPES.SUCCESS_ALERT_13
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Task ${this.currentId} was created successfully.`));
      this.task = task;
      this.taskService.selectTask(this.task);
      this.taskService.publishUpdatedTask(task);
      this.router.navigate([`../${task.taskId}`], { relativeTo: this.route });
    }, err => {
      this.requestInProgressService.setRequestInProgress(false);
      // new Key ALERT_TYPES.DANGER_ALERT_2
      this.alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'There was an error while creating a new task.'));
    });
  }

  private addDateToTask() {
    const date = TaskanaDate.getDate();
    this.task.created = date;
    this.task.modified = date;
  }

  private cloneTask() {
    this.taskClone = { ...this.task };
    this.taskClone.customAttributes = this.task.customAttributes.slice(0);
    this.taskClone.callbackInfo = this.task.callbackInfo.slice(0);
    this.taskClone.primaryObjRef = { ...this.task.primaryObjRef };
  }
}
