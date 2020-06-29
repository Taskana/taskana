import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { QueryType } from '../../models/query-type';

@Component({
  selector: 'taskana-monitor-workbasket-report-query-switcher',
  templateUrl: './workbasket-report-query-switcher.component.html',
  styleUrls: ['./workbasket-report-query-switcher.component.scss']
})
export class WorkbasketReportQuerySwitcherComponent implements OnInit {
  @Output()
  queryChanged = new EventEmitter<QueryType>();

  selectedChartType: QueryType;
  monitorQueryPlannedDateType = QueryType.PlannedDate;
  monitorQueryDueDateType = QueryType.DueDate;

  ngOnInit() {
    this.selectedChartType = QueryType.DueDate;
    this.queryChanged.emit(QueryType.DueDate);
  }

  switch(queryType: QueryType) {
    this.selectedChartType = queryType;
    this.queryChanged.emit(queryType);
  }
}
