import {Component, Input, OnInit} from '@angular/core';
import {ReportData} from 'app/monitor/models/report-data';
import {ReportInfoDataIterable} from "../models/report-info-data";

@Component({
  selector: 'taskana-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {


  expHeaders: Array<number>;
  currentExpHeaders: number = 0;
  _sumRow: ReportInfoDataIterable;

  constructor() {
  }

  private _reportData: ReportData;

  get reportData(): ReportData {
    return this._reportData;
  }

  @Input()
  set reportData(reportData: ReportData) {
    this._reportData = reportData;
    this.expHeaders = new Array<number>(Object.keys(reportData.rows).length + 1).fill(0);
    this._sumRow = new ReportInfoDataIterable();
    this._sumRow.val = reportData.sumRow;
    this._sumRow.key = reportData.meta.totalDesc;
  }

  ngOnInit(): void {
  }

  expandHeader(depth: number, index: number) {
    this.expHeaders[index] = depth;
    this.currentExpHeaders = Math.max(...this.expHeaders);
  }


}
