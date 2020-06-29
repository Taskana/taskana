import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from 'app/monitor/services/rest-connector.service';
import { ChartData } from 'app/monitor/models/chart-data';
import { ReportData } from '../../models/report-data';
import { ChartColorsDefinition } from '../../models/chart-colors';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-monitor-classification-report',
  templateUrl: './classification-report.component.html',
  styleUrls: ['./classification-report.component.scss']
})
export class ClassificationReportComponent implements OnInit {
  reportData: ReportData;


  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<ChartData>;
  lineChartOptions: any = {
    responsive: true
  };

  lineChartColors = ChartColorsDefinition.getColors();

  constructor(
    private restConnectorService: RestConnectorService,
    private requestInProgressService: RequestInProgressService
  ) {
  }

  async ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);
    this.reportData = await this.restConnectorService.getClassificationTasksReport().toPromise();
    this.lineChartData = this.restConnectorService.getChartData(this.reportData);
    this.lineChartLabels = this.reportData.meta.header;
    this.requestInProgressService.setRequestInProgress(false);
  }

  getTitle(): string {
    return 'Tasks grouped by classification, querying by due date';
  }
}
