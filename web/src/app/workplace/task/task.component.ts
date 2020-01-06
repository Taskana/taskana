import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/models/workbasket';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Subscription } from 'rxjs';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';


@Component({
  selector: 'taskana-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit, OnDestroy {

  routeSubscription: Subscription;
  requestInProgress = false;

  regex = /\${(.*?)}/g
  address = 'https://bing.com/';
  link: SafeResourceUrl;

  task: Task = null;
  workbaskets: Workbasket[];


  constructor(private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private classificationService: ClassificationsService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe(params => {
      const {id} = params;
      this.getTask(id);
    });
  }

  async getTask(id: string) {
    this.requestInProgress = true;
    this.task = await this.taskService.getTask(id).toPromise();
    const classification = await this.classificationService.getClassification(this.task.classificationSummaryResource.classificationId);
    this.address = this.extractUrl(classification.applicationEntryPoint) || `${this.address}/?q=${this.task.name}`;
    this.link = this.sanitizer.bypassSecurityTrustResourceUrl(this.address);
    this.getWorkbaskets();
    this.requestInProgress = false;

  }

  getWorkbaskets() {
    this.requestInProgress = true;
    this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
      this.requestInProgress = false;
      this.workbaskets = workbaskets.workbaskets;

      let index = -1;
      for (let i = 0; i < this.workbaskets.length; i++) {
        if (this.workbaskets[i].name === this.task.workbasketSummaryResource.name) {
          index = i;
        }
      }
      if (index !== -1) {
        this.workbaskets.splice(index, 1);
      }
    });
  }

  transferTask(workbasket: Workbasket) {
    this.requestInProgress = true;
    this.taskService.transferTask(this.task.taskId, workbasket.workbasketId).subscribe(
      task => {
        this.requestInProgress = false;
        this.task = task
      }
    );
    this.navigateBack();
  }

  completeTask() {
    this.requestInProgress = true;
    this.taskService.completeTask(this.task.taskId).subscribe(
      task => {
        this.requestInProgress = false;
        this.task = task;
        this.taskService.publishUpdatedTask(task);
        this.navigateBack();
      }
    );
  }

  navigateBack() {
    this.router.navigate([{ outlets: { detail: `taskdetail/${this.task.taskId}` } }], { relativeTo: this.route.parent });
  }

  private extractUrl(url: string): string {
    const me = this;
    const extractedExpressions = url.match(this.regex);
    if (!extractedExpressions) { return url; }
    extractedExpressions.forEach(expression => {
      const parameter = expression.substring(2, expression.length - 1);
      let objectValue: any = me;
      parameter.split('.').forEach(property => {
        objectValue = this.getReflectiveProperty(objectValue, property);
      })
      url = url.replace(expression, objectValue);
    })
    return url;
  }

  private getReflectiveProperty(scope: any, property: string) {
    return Reflect.get(scope, property)
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }
}
