import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Task } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-status-details',
  templateUrl: './task-status-details.component.html',
  styleUrls: ['./task-status-details.component.scss']
})
export class TaskStatusDetailsComponent implements OnInit {
  @Input() task: Task;
  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  ngOnInit() {}
}
