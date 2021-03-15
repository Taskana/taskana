import { Component, Input, OnInit } from '@angular/core';
import { CustomAttribute } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-attribute-value',
  templateUrl: './task-attribute-value.component.html',
  styleUrls: ['./task-attribute-value.component.scss']
})
export class TaskAttributeValueComponent implements OnInit {
  @Input() callbackInfo = false;
  @Input() attributes: CustomAttribute[] = [];

  ngOnInit() {}

  addAttribute(): void {
    this.attributes.push({ key: '', value: '' });
  }

  removeAttribute(idx: number): void {
    this.attributes.splice(idx, 1);
  }
}
