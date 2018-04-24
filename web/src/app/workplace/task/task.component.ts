import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../models/task';
import { Workbasket } from 'app/models/workbasket';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TaskService } from '../services/task.service';
import { WorkbasketService } from '../services/workbasket.service';


@Component({
  selector: 'taskana-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit {

  task: Task = null;
  link: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://duckduckgo.com/?q=');
  autoCompleteData: string[] = [];
  workbasket: string = null;
  workbasketKey: string;
  workbaskets: Workbasket[];

  private sub: any;

  constructor(private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    this.taskService.getTask(id).subscribe(
      t => {
        this.task = t;
        this.link = this.sanitizer.bypassSecurityTrustResourceUrl('https://duckduckgo.com/?q=' + this.task.name);
        this.workbasketService.getAllWorkBaskets().subscribe(w => {
          this.workbaskets = w['_embedded']['workbaskets'];
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
      this.taskService.transferTask(this.task.taskId, this.workbasketKey).subscribe(
        task => {
          this.task = task
        });
      this.navigateBack();
    }
  }

  cancelTask() {
    this.navigateBack();
  }

  completeTask() {
    this.taskService.completeTask(this.task.taskId).subscribe(
      task => {
        this.task = task
      });
    this.navigateBack();
  }

  private navigateBack() {
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }
}
