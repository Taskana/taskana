import { Component, Input, OnInit } from '@angular/core';
import { Task } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-details-custom-fields',
  templateUrl: './custom-fields.component.html'
})
export class TaskdetailsCustomFieldsComponent implements OnInit {

  @Input() task: Task;

  constructor() {
  }

  ngOnInit() {
  }

}
