import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { ReportData } from '../../models/report-data';
import { ChartData } from 'app/monitor/models/chart-data';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { map } from 'rxjs/internal/operators/map';

const monitorUrl = '/v1/monitor/';

@Injectable()
export class RestConnectorService {


  constructor(private httpClient: HttpClient) {
  }

  getTaskStatusReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl + monitorUrl
      + 'tasks-status-report?states=READY,CLAIMED,COMPLETED');
  }

  getWorkbasketStatistics(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl
      + monitorUrl + 'tasks-workbasket-report?daysInPast=5&states=READY,CLAIMED,COMPLETED');
  }

  getClassificationTasksReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl
      + monitorUrl + 'tasks-classification-report');
  }

  getDailyEntryExitReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl
      + monitorUrl + 'timestamp-report');
  }

  getChartData(source: ReportData): Array<ChartData> {
    const result = new Array<ChartData>();

    Object.keys(source.rows).forEach(key => {
      const rowData = new ChartData();

      rowData.label = key;
      rowData.data = new Array<number>();

      source.meta.header.forEach((headerValue: string) => {
        rowData.data.push(source.rows[key].cells[headerValue]);
      });

      result.push(rowData)
    });

    return result;
  }

  getChartHeaders(source: ReportData): Array<string> {
    const result = new Array<string>();
    source.meta.header.forEach((header: string) => {
      result.push(header);
    })
    return result;
  }
}
