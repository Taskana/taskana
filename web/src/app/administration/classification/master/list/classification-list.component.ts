import {Component, OnInit} from '@angular/core';
import {SelectionToImport} from '../../../enums/SelectionToImport';

@Component({
  selector: 'taskana-classification-list',
  templateUrl: './classification-list.component.html',
  styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit {

  selectionToImport = SelectionToImport.CLASSIFICATIONS;
  requestInProgress = false;

  constructor() {
  }

  ngOnInit() {
  }
}
