import { Component, Input, OnInit } from '@angular/core';
import { ReportType } from './reportType';
import { ReportData } from 'app/monitor/models/report-data';
import { RestConnectorService } from 'app/monitor/services/restConnector/rest-connector.service';

@Component({
  selector: 'taskana-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {

  @Input()
  type: ReportType;
  @Input()
  reportData: ReportData

  reportType = ReportType;

  constructor(private restConnector: RestConnectorService) {
  }

  ngOnInit(): void {

  }

  getClassificationHeadersText(header: string): string {
    return this.restConnector.parseClassificationHeadersText(header, this.type === ReportType.WorkbasketStatus ? true : false);
  }
}
