import {Component, OnInit} from '@angular/core';
import {Task} from '../models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from '../services/task.service';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit {
  task: Task = null;
  requestInProgress = false;

  private routeSubscription: Subscription;

  constructor(private route: ActivatedRoute,
              private taskService: TaskService,
              private router: Router) {
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
    });
  }

  openTask(taskId: string) {
    this.router.navigate([{outlets: {detail: `task/${taskId}`}}], {relativeTo: this.route.parent});
  }
}
