import { Component, OnInit } from '@angular/core';
import { ImportType } from 'app/models/import-type';

@Component({
  selector: 'taskana-classification-list',
  templateUrl: './classification-list.component.html',
  styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit {

  selectionToImport = ImportType.CLASSIFICATIONS;
  requestInProgress = false;

  constructor() {
  }

  ngOnInit() {
  }
}
