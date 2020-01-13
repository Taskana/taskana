import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { ChartData } from 'app/monitor/models/chart-data';
import { ReportData } from '../../models/report-data';

const monitorUrl = '/v1/monitor/';

@Injectable()
export class RestConnectorService {
  constructor(private httpClient: HttpClient) {
  }

  getTaskStatusReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.taskanaRestUrl + monitorUrl
       }tasks-status-report?states=READY,CLAIMED,COMPLETED`);
  }

  getWorkbasketStatisticsQueryingByDueDate(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.taskanaRestUrl
      + monitorUrl}tasks-workbasket-report?states=READY,CLAIMED,COMPLETED`);
  }

  getWorkbasketStatisticsQueryingByPlannedDate(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.taskanaRestUrl
       }/v1/monitor/tasks-workbasket-planned-date-report?daysInPast=7&states=READY,CLAIMED,COMPLETED`);
  }

  getClassificationTasksReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.taskanaRestUrl
      + monitorUrl}tasks-classification-report`);
  }

  getDailyEntryExitReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.taskanaRestUrl
      + monitorUrl}timestamp-report`);
  }

  getChartData(source: ReportData): Array<ChartData> {
    return source.rows.map(row => {
      const rowData = new ChartData();
      rowData.label = row.desc[0];
      rowData.data = row.cells;
      return rowData;
    });
  }
}
