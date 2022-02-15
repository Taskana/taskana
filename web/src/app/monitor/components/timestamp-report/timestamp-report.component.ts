import { Component, OnInit } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { MonitorService } from '../../services/monitor.service';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-monitor-timestamp-report',
  templateUrl: './timestamp-report.component.html',
  styleUrls: ['./timestamp-report.component.scss']
})
export class TimestampReportComponent implements OnInit {
  reportData: ReportData;

  constructor(
    private restConnectorService: MonitorService,
    private requestInProgressService: RequestInProgressService
  ) {}

  ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);
    this.restConnectorService.getDailyEntryExitReport().subscribe((data: ReportData) => {
      this.reportData = data;
      this.requestInProgressService.setRequestInProgress(false);
    });
  }
}
