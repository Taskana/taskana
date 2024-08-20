import { Component, OnInit } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';
import { MonitorService } from '../../services/monitor.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'kadai-monitor-task-report',
  templateUrl: './task-report.component.html',
  styleUrls: ['./task-report.component.scss']
})
export class TaskReportComponent implements OnInit {
  pieChartLabels: string[];
  pieChartData: number[] = [];
  pieChartType = 'pie';
  reportData: ReportData;
  private destroy$ = new Subject<void>();

  constructor(private monitorService: MonitorService, private requestInProgressService: RequestInProgressService) {}

  ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);
    this.monitorService
      .getTaskStatusReport()
      .pipe(takeUntil(this.destroy$))
      .subscribe((report) => {
        this.reportData = report;
        this.pieChartLabels = this.reportData.meta.header;
        this.reportData.sumRow[0].cells.forEach((cell) => {
          this.pieChartData.push(cell);
        });
        this.requestInProgressService.setRequestInProgress(false);
      });
  }

  getTitle(): string {
    return 'Tasks status grouped by domain';
  }
}
