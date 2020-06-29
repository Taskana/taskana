import { Component, OnInit } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { RestConnectorService } from '../../services/rest-connector.service';

@Component({
  selector: 'taskana-monitor-timestamp-report',
  templateUrl: './timestamp-report.component.html',
  styleUrls: ['./timestamp-report.component.scss']
})
export class TimestampReportComponent implements OnInit {
  reportData: ReportData;

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getDailyEntryExitReport().subscribe((data: ReportData) => {
      this.reportData = data;
    });
  }
}
