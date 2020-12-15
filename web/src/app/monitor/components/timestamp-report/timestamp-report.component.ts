import { Component, OnInit } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { MonitorService } from '../../services/monitor.service';

@Component({
  selector: 'taskana-monitor-timestamp-report',
  templateUrl: './timestamp-report.component.html',
  styleUrls: ['./timestamp-report.component.scss']
})
export class TimestampReportComponent implements OnInit {
  reportData: ReportData;

  constructor(private restConnectorService: MonitorService) {}

  ngOnInit() {
    this.restConnectorService.getDailyEntryExitReport().subscribe((data: ReportData) => {
      this.reportData = data;
    });
  }
}
