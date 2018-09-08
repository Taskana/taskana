import { Component, Input, OnInit } from '@angular/core';
import { Task } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-details-general-fields',
  templateUrl: './general-fields.component.html'
})
export class TaskdetailsGeneralFieldsComponent implements OnInit {

  @Input() task: Task;

  constructor() {
  }

  ngOnInit() {
  }

}
