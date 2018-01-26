import { Component, OnInit } from '@angular/core';
import { RestConnectorService } from '../service/rest-connector.service';
import { WorkbasketCounter } from '../model/workbasket-counter';
import { WorkbasketCounterData } from '../model/workbasket-counter-data';

@Component({
  selector: 'workbasket',
  templateUrl: './workbasket.component.html',
  styleUrls: ['./workbasket.component.scss'],
  providers: [RestConnectorService]
})
export class WorkbasketComponent implements OnInit {

  constructor(private restConnectorService: RestConnectorService) { }

  private counter: WorkbasketCounter;
  isDataAvailable: boolean = false;
  public lineChartLabels: Array<any>;
  public lineChartLegend: boolean = true;
  public lineChartType: string = 'line';
  // lineChart
  public lineChartData: Array<WorkbasketCounterData>;

  ngOnInit() {
    this.restConnectorService.getWorkbasketStatistics().subscribe(data => {
      console.log(data);
      console.log(this.lineChartData);
      this.lineChartLabels = data.dates;
      this.lineChartData = data.data;
      this.isDataAvailable = true;
      console.log(this.lineChartData);
    });
  }



  public lineChartOptions: any = {
    responsive: true
  };
  public lineChartColors: Array<any> = [
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

}
