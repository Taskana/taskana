import {Component, OnInit} from '@angular/core';
import {Task} from '../models/task';
import {ActivatedRoute} from '@angular/router';
import {TaskService} from '../services/task.service';
import {Location} from '@angular/common';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit {
  task: Task = null;

  constructor(private route: ActivatedRoute,
              private taskService: TaskService,
              private location: Location) {
  }

  ngOnInit() {
    this.getTask();
  }

  getTask(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.taskService.getTask(id).subscribe(task => {
      this.task = task
    });
  }

  goBack(): void {
    this.location.back();
  }
}
