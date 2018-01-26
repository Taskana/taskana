import { Component, Input, OnInit } from '@angular/core';
import { Task } from '../model/task';

@Component({
  selector: 'taskdetails',
  templateUrl: './taskdetails.component.html',
  styleUrls: ['./taskdetails.component.scss']
})
export class TaskdetailsComponent implements OnInit {

  @Input()
  task: Task = null;

  constructor() {
  }

  ngOnInit() {
  }
}
