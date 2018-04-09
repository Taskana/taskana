import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'taskana-classification-types-selector',
  templateUrl: './classification-types-selector.component.html',
  styleUrls: ['./classification-types-selector.component.scss']
})
export class ClassificationTypesSelectorComponent implements OnInit {

  @Input() classificationTypes: Array<string> = [];
  @Input()
  classificationTypeSelected: string = undefined;
  @Output()
  classificationTypeSelectedChange = new EventEmitter<string>();
  @Output()
  classificationTypeChanged = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  select(value: string) {
    this.classificationTypeSelected = value;
    this.classificationTypeChanged.emit(value);
  }
}
