import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'taskana-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.scss']
})
export class DropdownComponent implements OnInit {
  @Input() itemSelected: any;
  @Input() list: Array<any>;
  @Output() performClassification = new EventEmitter<any>();

  ngOnInit(): void {
  }

  selectItem(item: any) {
    this.itemSelected = item;
    this.performClassification.emit(item);
  }
}
