import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { State } from 'app/models/state';
import { ReportData } from '../../models/report-data';
import { ChartData } from 'app/monitor/models/chart-data';
import { TaskanaDate } from 'app/shared/util/taskana.date';

@Injectable()
export class RestConnectorService {

  constructor(private httpClient: HttpClient) {
  }

  getTaskStatusReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl + '/v1/monitor/tasks-status-report?states=READY,CLAIMED,COMPLETED')
  }

  getWorkbasketStatistics(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl
      + '/v1/monitor/tasks-workbasket-report?daysInPast=5&states=READY,CLAIMED,COMPLETED');
  }

  getClassificationTasksReport(): Observable<ReportData> {
    return this.httpClient.get<ReportData>(environment.taskanaRestUrl
      + '/v1/monitor/tasks-classification-report');
  }

  getChartData(source: ReportData): Array<ChartData> {
    const result = new Array<ChartData>();

    Object.keys(source.rows).forEach(key => {
      const rowData = new ChartData();

      rowData.label = key;
      rowData.data = new Array<number>();

      source.meta.header.forEach((headerValue: string) => {
        rowData.data.push(source.rows[key].cells[headerValue]);
      })

      result.push(rowData)
    })

    return result;
  }

  getChartHeaders(source: ReportData, dateFormat = false): Array<string> {
    const result = new Array<string>();
    source.meta.header.forEach((header: string) => {
      result.push(this.parseClassificationHeadersText(header, dateFormat));
    })
    return result;
  }

  parseClassificationHeadersText(header: string, dateFormat = false): string {
    if (dateFormat) {
      header = this.dateParseHeaders(header);
    } else {
      header = this.defaultParseHeaders(header);
    }
    return header;
  }

  defaultParseHeaders(header: string): string {
    if (header.indexOf('(-2147483648,-10)') !== -1) {
      header = '< -10';
    } else if (header.indexOf('(-10,-5)') !== -1) {
      header = '[10 ... 5]';
    } else if (header.indexOf('(-4,-4)') !== -1) {
      header = '-4';
    } else if (header.indexOf('(-3,-3)') !== -1) {
      header = '-3';
    } else if (header.indexOf('(-2,-2)') !== -1) {
      header = '-2';
    } else if (header.indexOf('(-1,-1)') !== -1) {
      header = '-1';
    } else if (header.indexOf('(0,0)') !== -1) {
      header = '0';
    } else if (header.indexOf('(1,1)') !== -1) {
      header = '1';
    } else if (header.indexOf('(2,2)') !== -1) {
      header = '2';
    } else if (header.indexOf('(3,3)') !== -1) {
      header = '3';
    } else if (header.indexOf('(4,4)') !== -1) {
      header = '4';
    } else if (header.indexOf('(5,10)') !== -1) {
      header = '[5 ... 10]';
    } else if (header.indexOf('(10,2147483647)') !== -1) {
      header = '> 10';
    }
    return header;
  }


  dateParseHeaders(header: string): string {
    const pattern = /(-?\d,-?\d)/;
    const regexResult = header.match(pattern);
    const offset = regexResult[0].split(',')[0];
    const date = new Date();
    date.setDate(date.getDate() + +offset)
    return TaskanaDate.convertSimpleDate(date);
  }
}
