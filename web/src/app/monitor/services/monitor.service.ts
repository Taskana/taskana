import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { ChartData } from 'app/monitor/models/chart-data';
import { ReportData } from '../models/report-data';
import { asUrlQueryString } from '../../shared/util/query-parameters-v2';
import { TaskState } from '../../shared/models/task-state';

const monitorUrl = '/v1/monitor';

@Injectable()
export class MonitorService {
  constructor(private httpClient: HttpClient) {}

  getTaskStatusReport(): Observable<ReportData> {
    const queryParams = {
      states: [TaskState.READY, TaskState.CLAIMED, TaskState.COMPLETED]
    };
    return this.httpClient.get<ReportData>(
      `${environment.kadaiRestUrl + monitorUrl}/task-status-report${asUrlQueryString(queryParams)}`
    );
  }

  getWorkbasketStatisticsQueryingByDueDate(): Observable<ReportData> {
    const queryParams = {
      states: [TaskState.READY, TaskState.CLAIMED, TaskState.COMPLETED]
    };
    return this.httpClient.get<ReportData>(
      `${environment.kadaiRestUrl + monitorUrl}/workbasket-report${asUrlQueryString(queryParams)}`
    );
  }

  getWorkbasketStatisticsQueryingByPlannedDate(): Observable<ReportData> {
    const queryParams = {
      'task-timetamp': 'PLANNED',
      states: [TaskState.READY, TaskState.CLAIMED, TaskState.COMPLETED]
    };
    return this.httpClient.get<ReportData>(
      `${environment.kadaiRestUrl + monitorUrl}/workbasket-report${asUrlQueryString(queryParams)}`
    );
  }

  getClassificationTasksReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.kadaiRestUrl + monitorUrl}/classification-report`);
  }

  getDailyEntryExitReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(`${environment.kadaiRestUrl + monitorUrl}/timestamp-report`);
  }

  getChartData(source: ReportData): ChartData[] {
    return source.rows.map((row) => {
      const rowData = new ChartData();
      [rowData.label] = row.desc;
      rowData.data = row.cells;
      return rowData;
    });
  }

  getTasksByPriorityReport(type: string[], priority: any[], customFilters: {} = {}): Observable<ReportData> {
    const queryParams = {
      'workbasket-type': type,
      state: 'READY',
      columnHeader: priority,
      ...customFilters
    };

    return this.httpClient.get<ReportData>(
      `${environment.kadaiRestUrl + monitorUrl}/workbasket-priority-report${asUrlQueryString(queryParams)}`
    );
  }
}
