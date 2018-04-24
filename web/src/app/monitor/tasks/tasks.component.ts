import { Component, OnInit } from '@angular/core';
import { ReportType } from '../report/reportType';
import { RestConnectorService } from '../services/restConnector/rest-connector.service';

@Component({
  selector: 'taskana-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
})
export class TasksComponent implements OnInit {


  pieChartLabels: string[] = ['Ready', 'Claimed', 'Completed'];
  pieChartData: number[] = [];
  pieChartType = 'pie';
  isDataAvailable = false;
  reportType = ReportType.WorkbasketStatus;

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getTaskStatistics().subscribe(data => {
      if (data.find(x => x.state === 'READY') !== null) {
        this.pieChartData.push(data.find(x => x.state === 'READY').counter);
      } else {
        this.pieChartData.push(0);
      }
      if (data.find(x => x.state === 'CLAIMED') !== null) {
        this.pieChartData.push(data.find(x => x.state === 'CLAIMED').counter);
      } else {
        this.pieChartData.push(0);
      }
      if (data.find(x => x.state === 'COMPLETED') !== null) {
        this.pieChartData.push(data.find(x => x.state === 'COMPLETED').counter);
      } else {
        this.pieChartData.push(0);
      }
      this.isDataAvailable = true;
    });
  }
}
