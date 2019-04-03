import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ReportInfoDataIterable} from '../../models/report-info-data';
import {MapToIterable} from '../../../shared/pipes/mapToIterable/mapToIterable';

@Component({
  selector: 'taskana-report-row',
  templateUrl: './row.component.html',
  styleUrls: ['./row.component.scss']
})
export class ReportRowComponent implements OnInit {

  @Input()
  headers: Array<string>;
  @Input()
  bold = false;
  @Input()
  maxTableDepth = 0;
  @Output()
  expandedDepth: EventEmitter<number> = new EventEmitter<number>();
  currentDepth = 0;
  maxDepth: number;
  flatRows: Array<ReportInfoDataIterable>;

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

  constructor(private mapToIterable: MapToIterable) {
  }

  ngOnInit() {
  }

  toggleFold(index: number): void {
    const toggleRow = this.flatRows[index++];
    if (toggleRow.depth < this.maxDepth) {
      const firstChildRow = this.flatRows[index++];
      firstChildRow.display = !firstChildRow.display;

      let end = false;
      for (let i = index; i < this.flatRows.length && !end; i++) {
        const row = this.flatRows[i];
        end = row.depth <= toggleRow.depth;
        if (!end) {
          row.display = firstChildRow.display && row.depth === firstChildRow.depth;
        }
      }
      this.currentDepth = Math.max(...this.flatRows.filter(r => r.display).map(r => r.depth));
      this.expandedDepth.emit(this.currentDepth);
    }
  }

  range(depth: number): Array<null> {
    return new Array<null>(Math.max(depth, 0));
  }

  canRowCollapse(index: number) {
    return !this.flatRows[index + 1].display;
  }

  private flatten(row: ReportInfoDataIterable, depth: number): number {
    row.depth = depth;
    row.display = depth === 0;
    this.flatRows.push(row);
    if (row.val.foldableRows) {
      depth = Math.max(...this.mapToIterable.transform(row.val.foldableRows)
        .sort((a, b) => a.key.localeCompare(b.key))
        .map(r => this.flatten(r, depth + 1)));
    }
    return depth;
  }
}
