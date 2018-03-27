import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from '../service/rest-connector.service';
import { Report } from "../report/report.component";
import { ReportModel } from "../model/report";

@Component({
  selector: 'tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
  providers: [RestConnectorService]
})
export class TasksComponent implements OnInit {


  pieChartLabels: string[] = ['Ready', 'Claimed', 'Completed'];
  pieChartData: number[] = [];
  pieChartType: string = 'pie';
  isDataAvailable: boolean = false;
  report: Report;
  taskStatusReport: ReportModel;

  constructor(private restConnectorService: RestConnectorService) {
  }

  ngOnInit() {
    this.restConnectorService.getTaskStatistics().subscribe(data => {
      if (data.find(x => x.state == "READY") != null) {
        this.pieChartData.push(data.find(x => x.state == "READY").counter);
      } else {
        this.pieChartData.push(0);
      }
      if (data.find(x => x.state == "CLAIMED") != null) {
        this.pieChartData.push(data.find(x => x.state == "CLAIMED").counter);
      } else {
        this.pieChartData.push(0);
      }
      if (data.find(x => x.state == "COMPLETED") != null) {
        this.pieChartData.push(data.find(x => x.state == "COMPLETED").counter);
      } else {
        this.pieChartData.push(0);
      }
    });
    this.restConnectorService.getTaskStatusReport().subscribe(report => {
      this.taskStatusReport = report;
      this.isDataAvailable = true;
    })
  }
}
