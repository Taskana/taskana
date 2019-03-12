import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SortingModel} from '../../models/sorting';
import {Classification} from '../../models/classification';

@Component({
  selector: 'taskana-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.scss']
})
export class DropdownComponent implements OnInit {

  @Input() itemSelected: any;
  @Input() list: Array<any>;
  @Output() performClassification = new EventEmitter<any>();

  constructor() {
  }

  ngOnInit(): void {
  }

  selectItem(item: any) {
    this.itemSelected = item;
    this.performClassification.emit(item);
  }
}
