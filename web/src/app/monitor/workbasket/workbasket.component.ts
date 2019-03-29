import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from 'app/monitor/services/restConnector/rest-connector.service';
import { ChartColorsDefinition } from '../models/chart-colors';
import { ReportData } from 'app/monitor/models/report-data';
import {ChartData} from 'app/monitor/models/chart-data';

@Component({
  selector: 'taskana-monitor-workbaskets',
  templateUrl: './workbasket.component.html',
  styleUrls: ['./workbasket.component.scss'],
  providers: [RestConnectorService]
})
export class WorkbasketComponent implements OnInit {

  reportData: ReportData;


  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<ChartData>;
  lineChartOptions: any = {
    responsive: true
  };
  lineChartColors = ChartColorsDefinition.getColors();

  constructor(private restConnectorService: RestConnectorService) { }


  ngOnInit() {
    this.restConnectorService.getWorkbasketStatistics().subscribe((data: ReportData) => {
      this.reportData = data;
      this.lineChartLabels = this.restConnectorService.getChartHeaders(data);
      this.lineChartData = this.restConnectorService.getChartData(data);
    })
  }
}
