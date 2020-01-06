import {Component, OnInit} from '@angular/core';
import {MetaInfoData} from '../models/meta-info-data';
import {MonitorQueryType} from '../models/monitor-query-type';

@Component({
  selector: 'taskana-monitor-workbaskets',
  templateUrl: './monitor-workbaskets.component.html',
  styleUrls: ['./monitor-workbaskets.component.scss']
})
export class MonitorWorkbasketsComponent implements OnInit {

  metaInformation: MetaInfoData;
  showMonitorQueryPlannedDate: Boolean;
  showMonitorQueryDueDate: Boolean;

  constructor() {
  }

  ngOnInit() {
  }

  getMetaInformation(metaInformation: MetaInfoData) {
    this.metaInformation = metaInformation;
  }

  queryChanged(monitorQueryType: MonitorQueryType) {
    this.switchGraphicShowed(monitorQueryType);
  }

  getTitle(): string {
    return this.showMonitorQueryPlannedDate
      ? 'Tasks grouped by workbasket, querying by planned date'
      : 'Tasks grouped by workbasket, querying by due date';
  }

  private switchGraphicShowed(monitorQueryType: MonitorQueryType) {
    if (monitorQueryType === MonitorQueryType.PlannedDate) {
      this.showMonitorQueryPlannedDate = true;
      this.showMonitorQueryDueDate = false
    } else if (monitorQueryType === MonitorQueryType.DueDate) {
      this.showMonitorQueryPlannedDate = false;
      this.showMonitorQueryDueDate = true
    }
  }
}
