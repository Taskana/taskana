import {Component, OnInit} from '@angular/core';
import {ReportData} from '../models/report-data';
import {RestConnectorService} from '../services/restConnector/rest-connector.service';

@Component({
  selector: 'taskana-monitor-timestamp',
  templateUrl: './timestamp.component.html',
  styleUrls: ['./timestamp.component.scss']
})
export class TimestampComponent implements OnInit {

  reportData: ReportData;

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getDailyEntryExitReport().subscribe((data: ReportData) => {
      this.reportData = data;
    })
  }

}
