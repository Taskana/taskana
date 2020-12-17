import { Component, OnInit } from '@angular/core';
import { MetaInfoData } from '../../models/meta-info-data';
import { QueryType } from '../../models/query-type';
enum WorkbasketReports {
  DUE_DATE,
  PLANNED_DATE
}

@Component({
  selector: 'taskana-monitor-workbasket-report',
  templateUrl: './workbasket-report.component.html',
  styleUrls: ['./workbasket-report.component.scss']
})
export class WorkbasketReportComponent implements OnInit {
  metaInformation: MetaInfoData;
  selectedComponent: WorkbasketReports;

  ngOnInit() {}

  getMetaInformation(metaInformation: MetaInfoData) {
    this.metaInformation = metaInformation;
  }

  selectComponent(component) {
    this.selectedComponent = component;
  }
  getTitle(): string {
    return this.selectedComponent
      ? 'Tasks grouped by workbasket, querying by planned date'
      : 'Tasks grouped by workbasket, querying by due date';
  }
}
