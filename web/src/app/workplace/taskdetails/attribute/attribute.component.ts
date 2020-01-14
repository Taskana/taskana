import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CustomAttribute } from 'app/workplace/models/task';

@Component({
  selector: 'taskana-task-details-attributes',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.scss']
})
export class TaskdetailsAttributeComponent implements OnInit {
  @Input() callbackInfo = false;
  @Input() attributes: CustomAttribute[] = [];
  @Output() attributesChange: EventEmitter<CustomAttribute[]> = new EventEmitter<CustomAttribute[]>();

  ngOnInit() {
  }

  addAttribute(): void {
    this.attributes.push({ key: '', value: '' });
  }

  removeAttribute(idx: number): void {
    this.attributes.splice(idx, 1);
  }
}
