import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ReportInfoDataIterable} from "../../models/report-info-data";
import {MapToIterable} from "../../../shared/pipes/mapToIterable/mapToIterable";

@Component({
  selector: 'monitor-report-row',
  templateUrl: './row.component.html',
  styleUrls: ['./row.component.scss']
})
export class ReportRowComponent implements OnInit {

  @Input()
  headers: Array<string>;
  @Input()
  bold: boolean = false;
  @Input()
  maxTableDepth: number = 0;
  @Output()
  expandedDepth: EventEmitter<number> = new EventEmitter<number>();
  maxDepth: number;
  currentDepth: number = 0;
  flatRows: Array<ReportInfoDataIterable>;

  constructor(private mapToIterable: MapToIterable) {
  }


  private _row: ReportInfoDataIterable;

  get row(): ReportInfoDataIterable {
    return this._row;
  }

  @Input()
  set row(row: ReportInfoDataIterable) {
    this._row = row;
    this.flatRows = new Array<ReportInfoDataIterable>();
    this.maxDepth = this.flatten(row, 0);
  }

  ngOnInit() {
  }

  toggleFold(depth: number) {
    this.currentDepth = depth == this.currentDepth && depth < this.maxDepth ? depth + 1 : depth;
    this.expandedDepth.emit(this.currentDepth);
  }

  range(depth: number): Array<null> {
    return new Array<null>(Math.max(depth, 0));
  }

  private flatten(row: ReportInfoDataIterable, depth: number): number {
    row.depth = depth;
    this.flatRows.push(row);
    if (row.val.foldableRows) {
      depth = Math.max(...this.mapToIterable.transform(row.val.foldableRows)
        .sort((a, b) => a.key.localeCompare(b.key))
        .map(r => this.flatten(r, depth + 1)))
    }
    return depth;
  }
}
