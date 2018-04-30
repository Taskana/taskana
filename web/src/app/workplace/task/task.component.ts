import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Task} from 'app/workplace/models/task';
import {Workbasket} from 'app/models/workbasket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {TaskService} from 'app/workplace/services/task.service';
import {WorkbasketService} from 'app/workplace/services/workbasket.service';


@Component({
  selector: 'taskana-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit {
  task: Task = null;
  address = 'https://bing.com';
  link: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.address);
  autoCompleteData: string[] = [];
  workbasket: string = null;
  workbasketKey: string;
  workbaskets: Workbasket[];
  requestInProgress = false;

  constructor(private taskService: TaskService,
              private workbasketService: WorkbasketService,
              private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    this.getTask(id);
  }

  getTask(id: string) {
    this.requestInProgress = true;
    this.taskService.getTask(id).subscribe(
      task => {
        this.requestInProgress = false;
        this.task = task;
        this.link = this.sanitizer.bypassSecurityTrustResourceUrl(`${this.address}/?q=${this.task.name}`);
        this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
          this.workbaskets = workbaskets._embedded ? workbaskets._embedded.workbaskets : [];
          this.workbaskets.forEach(workbasket => {
            if (workbasket.key !== this.task.workbasketSummaryResource.key) {
              this.autoCompleteData.push(workbasket.name);
            }
          });
        });
      });
  }

  transferTask() {
    if (this.workbasket) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.workbasket) {
          this.workbasketKey = workbasket.key;
        }
      });

      this.requestInProgress = true;
      this.taskService.transferTask(this.task.taskId, this.workbasketKey).subscribe(
        task => {
          this.requestInProgress = false;
          this.task = task
        });
      this.navigateBack();
    }
  }

  cancelTask() {
    this.navigateBack();
  }

  completeTask() {
    this.requestInProgress = true;
    this.taskService.completeTask(this.task.taskId).subscribe(
      task => {
        this.requestInProgress = false;
        this.task = task
      });
    this.navigateBack();
  }

  private navigateBack() {
    this.router.navigate([{outlets: {detail: `taskdetail/${this.task.taskId}`}}], {relativeTo: this.route.parent});
  }
}
