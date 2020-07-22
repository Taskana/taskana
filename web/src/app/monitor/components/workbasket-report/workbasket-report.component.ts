import { Component, OnInit } from '@angular/core';
import { MetaInfoData } from '../../models/meta-info-data';
import { QueryType } from '../../models/query-type';

@Component({
  selector: 'taskana-monitor-workbasket-report',
  templateUrl: './workbasket-report.component.html',
  styleUrls: ['./workbasket-report.component.scss']
})
export class WorkbasketReportComponent implements OnInit {
  metaInformation: MetaInfoData;
  showMonitorQueryPlannedDate: Boolean;
  showMonitorQueryDueDate: Boolean;

  ngOnInit() {}

  getMetaInformation(metaInformation: MetaInfoData) {
    this.metaInformation = metaInformation;
  }

  queryChanged(monitorQueryType: QueryType) {
    this.switchGraphicShowed(monitorQueryType);
  }

  getTitle(): string {
    return this.showMonitorQueryPlannedDate
      ? 'Tasks grouped by workbasket, querying by planned date'
      : 'Tasks grouped by workbasket, querying by due date';
  }

  private switchGraphicShowed(monitorQueryType: QueryType) {
    if (monitorQueryType === QueryType.PlannedDate) {
      this.showMonitorQueryPlannedDate = true;
      this.showMonitorQueryDueDate = false;
    } else if (monitorQueryType === QueryType.DueDate) {
      this.showMonitorQueryPlannedDate = false;
      this.showMonitorQueryDueDate = true;
    }
  }
}
