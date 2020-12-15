import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { ChartData } from '../../models/chart-data';
import { ChartColorsDefinition } from '../../models/chart-colors';
import { MonitorService } from '../../services/monitor.service';
import { MetaInfoData } from '../../models/meta-info-data';

@Component({
  selector: 'taskana-monitor-workbasket-report-due-date',
  templateUrl: './workbasket-report-due-date.component.html',
  styleUrls: ['./workbasket-report-due-date.component.scss']
})
export class WorkbasketReportDueDateComponent implements OnInit {
  @Output()
  metaInformation = new EventEmitter<MetaInfoData>();

  reportData: ReportData;

  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<ChartData>;
  lineChartOptions: any = {
    responsive: true
  };

  lineChartColors = ChartColorsDefinition.getColors();

  constructor(private restConnectorService: MonitorService) {}

  async ngOnInit() {
    this.reportData = await this.restConnectorService.getWorkbasketStatisticsQueryingByDueDate().toPromise();
    this.metaInformation.emit(this.reportData.meta);
    this.lineChartLabels = this.reportData.meta.header;
    this.lineChartData = this.restConnectorService.getChartData(this.reportData);
  }
}
