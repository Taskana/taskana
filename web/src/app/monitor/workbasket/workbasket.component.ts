import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from 'app/monitor/services/restConnector/rest-connector.service';
import { WorkbasketCounter } from 'app/monitor/models/workbasket-counter';
import { WorkbasketCounterData } from 'app/monitor/models/workbasket-counter-data';

@Component({
  selector: 'taskana-workbasket',
  templateUrl: './workbasket.component.html',
  styleUrls: ['./workbasket.component.scss'],
  providers: [RestConnectorService]
})
export class WorkbasketComponent implements OnInit {

  isDataAvailable = false;
  lineChartLabels: Array<any>;
  lineChartLegend = true;
  lineChartType = 'line';
  lineChartData: Array<WorkbasketCounterData>;
  lineChartOptions: any = {
    responsive: true
  };
  lineChartColors: Array<any> = [
    { // grey
      backgroundColor: 'rgba(148,159,177,0.2)',
      borderColor: 'rgba(148,159,177,1)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    { // dark grey
      backgroundColor: 'rgba(77,83,96,0.2)',
      borderColor: 'rgba(77,83,96,1)',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    { // grey
      backgroundColor: 'rgba(148,159,177,0.2)',
      borderColor: 'rgba(148,159,177,1)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];

  private counter: WorkbasketCounter;

  constructor(private restConnectorService: RestConnectorService) { }

  ngOnInit() {
    this.restConnectorService.getWorkbasketStatistics().subscribe(data => {
      this.lineChartLabels = data.dates;
      this.lineChartData = data.data;
      this.isDataAvailable = true;
    });
  }
}
