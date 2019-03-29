import {Component, Input, OnInit} from '@angular/core';
import {ReportData} from 'app/monitor/models/report-data';

@Component({
  selector: 'taskana-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {

  @Input()
  reportData: ReportData;

  constructor() {
  }

  ngOnInit(): void {
  }

}
