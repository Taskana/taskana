import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { convertToCustomAttributes, CustomAttribute, Task } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-details-attributes',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.scss']
})
export class TaskdetailsAttributeComponent implements OnInit {

  @Input() task: Task;
  @Input() callbackInfo = false;
  attributes: CustomAttribute[] = [];

  @Output() notify: EventEmitter<CustomAttribute[]> = new EventEmitter<CustomAttribute[]>();

  constructor() {
  }

  ngOnInit() {
    if (this.task) {
      this.attributes = convertToCustomAttributes.bind(this.task)(this.callbackInfo);
      this.notify.emit(this.attributes);
    }
  }

  addAttribute(): void {
    this.attributes.push({ key: '', value: '' });
  }

  removeAttribute(idx: number): void {
    this.attributes.splice(idx, 1);
  }

}
