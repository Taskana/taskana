import { Component, Input, OnInit } from "@angular/core";
import { RestConnectorService } from "../service/rest-connector.service";
import { error } from "util";
import { ReportType } from "./reportType";

@Component({
  selector: 'report',
  templateUrl: './report.component.html'
})
export class Report implements OnInit {

  @Input()
  type: ReportType;

  meta: ReportMeta;
  /*
   * The keys of the rows object are unknown. They represent the name of that specific row.
   * Each row (value of rows Object) has two keys: 'cells:Object' and 'total:number'.
   * The keys of 'cells' are the same as 'meta.header:number'.
   * This also applies to sumRow.
   */
  rows: Object;
  sumRow: Object;


  private isDataAvailable: boolean = false;

  constructor(private restConnector: RestConnectorService) {
  }

  ngOnInit(): void {
    switch (this.type) {
      case ReportType.WorkbasketStatus:
        this.restConnector.getTaskStatusReport().subscribe(res => {
          this.meta = res['meta'];
          this.rows = res['rows'];
          this.sumRow = res['sumRow'];
          this.isDataAvailable = true;
        });
        break;
      default:
        error("unknown ReportType '" + this.type + "'");
    }
  }

}

class ReportMeta {
  name: string;
  date: string;
  header: Array<string>;
  rowDesc: string;
  totalDesc: string;

}
