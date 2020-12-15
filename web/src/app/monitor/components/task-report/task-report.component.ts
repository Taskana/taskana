import { Component, OnInit } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';
import { MonitorService } from '../../services/monitor.service';

@Component({
  selector: 'taskana-monitor-task-report',
  templateUrl: './task-report.component.html',
  styleUrls: ['./task-report.component.scss']
})
export class TaskReportComponent implements OnInit {
  pieChartLabels: string[];
  pieChartData: number[] = [];
  pieChartType = 'pie';
  reportData: ReportData;

  constructor(private restConnectorService: MonitorService) {}

  async ngOnInit() {
    this.reportData = await this.restConnectorService.getTaskStatusReport().toPromise();
    this.pieChartLabels = this.reportData.meta.header;
    this.reportData.sumRow[0].cells.forEach((c) => {
      this.pieChartData.push(c);
    });
  }

  getTitle(): string {
    return 'Tasks status grouped by domain';
  }
}
