import { Component, Input, OnInit } from "@angular/core";
import { ReportModel } from "../model/report";

@Component({
  selector: 'report',
  templateUrl: './report.component.html'
})
export class Report implements OnInit {

  @Input()
  model: ReportModel;

  constructor() {
  }

  ngOnInit(): void {

  }


}
