import {Component, OnInit} from '@angular/core';
import {RestConnectorService} from '../services/restConnector/rest-connector.service';
import {ReportData} from 'app/monitor/models/report-data';

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

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getTaskStatusReport().subscribe((data: ReportData) => {
      this.reportData = data;
      this.pieChartLabels = data.meta.header;
      data.sumRow[0].cells.forEach(c => this.pieChartData.push(c));

    })

  }
}
