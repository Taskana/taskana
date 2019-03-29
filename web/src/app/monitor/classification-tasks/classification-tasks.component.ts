import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from 'app/monitor/services/restConnector/rest-connector.service';
import { ReportData } from '../models/report-data';
import { ChartData } from 'app/monitor/models/chart-data';
import { ChartColorsDefinition } from '../models/chart-colors';

@Component({
  selector: 'taskana-monitor-classification-tasks',
  templateUrl: './classification-tasks.component.html',
  styleUrls: ['./classification-tasks.component.scss']
})
export class ClassificationTasksComponent implements OnInit {
  reportData: ReportData;


  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<ChartData>;
  lineChartOptions: any = {
    responsive: true
  };
  lineChartColors = ChartColorsDefinition.getColors();

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getClassificationTasksReport().subscribe((data: ReportData) => {
      this.reportData = data;
      this.lineChartData = this.restConnectorService.getChartData(data);
      this.lineChartLabels = this.restConnectorService.getChartHeaders(data);
    })
  }
}
