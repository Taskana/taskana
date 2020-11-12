import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { ChartData } from '../../models/chart-data';
import { ChartColorsDefinition } from '../../models/chart-colors';
import { RestConnectorService } from '../../services/rest-connector.service';
import { MetaInfoData } from '../../models/meta-info-data';

@Component({
  selector: 'taskana-monitor-workbasket-report-planned-date',
  templateUrl: './workbasket-report-planned-date.component.html',
  styleUrls: ['./workbasket-report-planned-date.component.scss']
})
export class WorkbasketReportPlannedDateComponent implements OnInit {
  @Output()
  metaInformation = new EventEmitter<MetaInfoData>();

  reportData: ReportData;

  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<ChartData>;
  lineChartOptions: any = {
    responsive: true,
    scales: { xAxes: [{}], yAxes: [{}] }
  };

  lineChartColors = ChartColorsDefinition.getColors();

  constructor(private restConnectorService: RestConnectorService) {}

  async ngOnInit() {
    this.reportData = await this.restConnectorService.getWorkbasketStatisticsQueryingByPlannedDate().toPromise();
    this.metaInformation.emit(this.reportData.meta);
    this.lineChartLabels = this.reportData.meta.header;
    this.lineChartData = this.restConnectorService.getChartData(this.reportData);
  }
}
