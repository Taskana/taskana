import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MonitorQueryType } from '../../models/monitor-query-type';

@Component({
  selector: 'taskana-monitor-workbasket-query-switcher',
  templateUrl: './monitor-workbasket-query-switcher.component.html',
  styleUrls: ['./monitor-workbasket-query-switcher.component.scss']
})
export class MonitorWorkbasketQuerySwitcherComponent implements OnInit {
  @Output()
  queryChanged = new EventEmitter<MonitorQueryType>();

  selectedChartType: MonitorQueryType;
  monitorQueryPlannedDateType = MonitorQueryType.PlannedDate;
  monitorQueryDueDateType = MonitorQueryType.DueDate;

  ngOnInit() {
    this.selectedChartType = MonitorQueryType.DueDate;
    this.queryChanged.emit(MonitorQueryType.DueDate);
  }

  switch(queryType: MonitorQueryType) {
    this.selectedChartType = queryType;
    this.queryChanged.emit(queryType);
  }
}
