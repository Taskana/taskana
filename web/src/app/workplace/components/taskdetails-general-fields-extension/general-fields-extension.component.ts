import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Task } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-details-general-fields-extension',
  templateUrl: './general-fields-extension.component.html',
  styleUrls: ['./general-fields-extension.component.scss']
})
export class GeneralFieldsExtensionComponent implements OnInit {
  @Input() task: Task;
  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  ngOnInit() {}
}
