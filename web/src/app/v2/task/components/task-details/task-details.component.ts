import { Component, OnInit } from '@angular/core';
import { Select } from '@ngxs/store';
import { TaskSelector } from '@task/store/task.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'taskana-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.scss']
})
export class TaskDetailsComponent implements OnInit {
  @Select(TaskSelector.selectedTask) selectedTask$: Observable<Task | null>;
  constructor() {}

  ngOnInit(): void {}
}
