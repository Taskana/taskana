import { Component, OnInit } from '@angular/core';
import { ReportType } from '../report/reportType';
import { RestConnectorService } from '../services/restConnector/rest-connector.service';
import { ReportData } from 'app/monitor/models/report-data';

@Component({
  selector: 'taskana-monitor-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
})
export class TasksComponent implements OnInit {


  pieChartLabels: string[];
  pieChartData: number[] = [];
  pieChartType = 'pie';
  reportData: ReportData
  reportType = ReportType.TasksStatus;

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getTaskStatusReport().subscribe((data: ReportData) => {
      this.reportData = data;
      this.pieChartLabels = Object.keys(data.sumRow.cells);
      Object.keys(data.sumRow.cells).forEach(key => {
        this.pieChartData.push(data.sumRow.cells[key]);
      })

    })

  }
}
