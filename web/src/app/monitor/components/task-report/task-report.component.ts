import { Component, OnInit } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';
import { RestConnectorService } from '../../services/rest-connector.service';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-monitor-task-report',
  templateUrl: './task-report.component.html',
  styleUrls: ['./task-report.component.scss'],
})
export class TaskReportComponent implements OnInit {
  pieChartLabels: string[];
  pieChartData: number[] = [];
  pieChartType = 'pie';
  reportData: ReportData;

  constructor(
    private restConnectorService: RestConnectorService,
    private requestInProgressService: RequestInProgressService
  ) {
  }

  async ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);
    this.reportData = await this.restConnectorService.getTaskStatusReport().toPromise();
    this.pieChartLabels = this.reportData.meta.header;
    this.reportData.sumRow[0].cells.forEach(c => {
      this.pieChartData.push(c);
    });
    this.requestInProgressService.setRequestInProgress(false);
  }

  getTitle(): string {
    return 'Tasks status grouped by domain';
  }
}
