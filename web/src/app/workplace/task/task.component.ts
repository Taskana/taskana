import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Task} from 'app/workplace/models/task';
import {Workbasket} from 'app/models/workbasket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {TaskService} from 'app/workplace/services/task.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {Subscription} from 'rxjs';


@Component({
  selector: 'taskana-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit, OnDestroy {

  routeSubscription: Subscription;
  requestInProgress = false;

  address = 'https://bing.com';
  link: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.address);

  task: Task = null;
  workbaskets: Workbasket[];


  constructor(private taskService: TaskService,
              private workbasketService: WorkbasketService,
              private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe(params => {
      const id = params['id'];
      this.getTask(id);
    });
  }

  getTask(id: string) {
    this.requestInProgress = true;
    this.taskService.getTask(id).subscribe(
      task => {
        this.requestInProgress = false;
        this.task = task;
        this.link = this.sanitizer.bypassSecurityTrustResourceUrl(`${this.address}/?q=${this.task.name}`);
        this.getWorkbaskets();
      });
  }

  getWorkbaskets() {
    this.requestInProgress = true;
    this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
      this.requestInProgress = false;
      this.workbaskets = workbaskets._embedded ? workbaskets._embedded.workbaskets : [];

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
      });
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
      });
  }

  navigateBack() {
    this.router.navigate([{outlets: {detail: `taskdetail/${this.task.taskId}`}}], {relativeTo: this.route.parent});
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }
}
