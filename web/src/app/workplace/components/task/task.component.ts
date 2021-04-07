import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/shared/models/workbasket';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Subscription } from 'rxjs';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { take } from 'rxjs/operators';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit, OnDestroy {
  routeSubscription: Subscription;

  regex = /\${(.*?)}/g;
  address = 'https://bing.com/';
  link: SafeResourceUrl;

  task: Task = null;
  workbaskets: Workbasket[];

  constructor(
    private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private classificationService: ClassificationsService,
    private requestInProgressService: RequestInProgressService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe((params) => {
      const { id } = params;
      this.getTask(id);

      this.requestInProgressService.setRequestInProgress(true);
      this.taskService
        .claimTask(id)
        .pipe(take(1))
        .subscribe((task) => {
          this.task = task;
          this.taskService.publishUpdatedTask(task);
          this.requestInProgressService.setRequestInProgress(false);
        });
    });
  }

  async getTask(id: string) {
    this.requestInProgressService.setRequestInProgress(true);
    this.task = await this.taskService.getTask(id).toPromise();
    this.taskService.selectTask(this.task);
    const classification = await this.classificationService
      .getClassification(this.task.classificationSummary.classificationId)
      .toPromise();
    this.address = this.extractUrl(classification.applicationEntryPoint) || `${this.address}/?q=${this.task.name}`;
    this.link = this.sanitizer.bypassSecurityTrustResourceUrl(this.address);
    this.getWorkbaskets();
    this.requestInProgressService.setRequestInProgress(false);
  }

  getWorkbaskets() {
    this.requestInProgressService.setRequestInProgress(true);
    this.workbasketService.getAllWorkBaskets().subscribe((workbaskets) => {
      this.requestInProgressService.setRequestInProgress(false);
      this.workbaskets = workbaskets.workbaskets;

      const index = this.workbaskets.findIndex((workbasket) => workbasket.name === this.task.workbasketSummary.name);
      if (index !== -1) {
        this.workbaskets.splice(index, 1);
      }
    });
  }

  transferTask(workbasket: Workbasket) {
    this.requestInProgressService.setRequestInProgress(true);
    this.taskService.transferTask(this.task.taskId, workbasket.workbasketId).subscribe((task) => {
      this.requestInProgressService.setRequestInProgress(false);
      this.task = task;
    });
    this.navigateBack();
  }

  completeTask() {
    this.requestInProgressService.setRequestInProgress(true);
    this.taskService.completeTask(this.task.taskId).subscribe((task) => {
      this.requestInProgressService.setRequestInProgress(false);
      this.task = task;
      this.taskService.publishUpdatedTask(task);
      this.navigateBack();
    });
  }

  cancelClaimTask() {
    this.requestInProgressService.setRequestInProgress(true);
    this.taskService
      .cancelClaimTask(this.task.taskId)
      .pipe(take(1))
      .subscribe((task) => {
        this.task = task;
        this.taskService.publishUpdatedTask(task);
        this.requestInProgressService.setRequestInProgress(false);
      });
    this.navigateBack();
  }

  navigateBack() {
    this.router.navigate([{ outlets: { detail: `taskdetail/${this.task.taskId}` } }], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge'
    });
  }

  private extractUrl(url: string): string {
    const me = this;
    const extractedExpressions = url.match(this.regex);
    if (!extractedExpressions) {
      return url;
    }
    let extractedUrl = url;
    extractedExpressions.forEach((expression) => {
      const parameter = expression.substring(2, expression.length - 1);
      let objectValue: any = me;
      parameter.split('.').forEach((property) => {
        objectValue = this.getReflectiveProperty(objectValue, property);
      });
      extractedUrl = extractedUrl.replace(expression, objectValue);
    });
    return extractedUrl;
  }

  private getReflectiveProperty(scope: any, property: string) {
    return Reflect.get(scope, property);
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }
}
