import { Component, Input, OnChanges } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';
import { ReportRow } from '../../models/report-row';

@Component({
  selector: 'taskana-monitor-report-table',
  templateUrl: './report-table.component.html',
  styleUrls: ['./report-table.component.scss']
})
export class ReportTableComponent implements OnChanges {
  @Input()
  reportData: ReportData;

  fullReportData: ReportData;
  fullRowsData: ReportRow[][];
  currentExpHeaders = 0;

  ngOnChanges() {
    this.fullReportData = { ...this.reportData };
    this.fullRowsData = this.fullReportData.rows?.reduce((resultArray: ReportRow[][], item, index) => {
      const itemsPerChunk = 20;
      if (this.fullReportData.rows.length > itemsPerChunk) {
        const chunkIndex = Math.floor(index / itemsPerChunk);

        if (!resultArray[chunkIndex]) {
          resultArray[chunkIndex] = []; // start a new chunk
        }

        resultArray[chunkIndex].push(item);
      } else {
        return [this.fullReportData.rows];
      }
      return resultArray;
    }, []);
    if (this.fullRowsData) {
      this.reportData.rows = this.fullRowsData[0];
      this.fullRowsData.splice(0, 1);
    }
  }

  addRows() {
    if (typeof this.fullRowsData !== 'undefined' && this.fullRowsData[0]) {
      this.reportData.rows = [...this.reportData.rows, ...this.fullRowsData[0]];
      this.fullRowsData.splice(0, 1);
    }
  }

  toggleFold(indexNumber: number, sumRow: boolean = false) {
    let rows = sumRow ? this.reportData.sumRow : this.reportData.rows;
    let index = indexNumber;
    const toggleRow = rows[index];
    if (toggleRow.depth < this.reportData.meta.rowDesc.length - 1) {
      const firstChildRow = rows[(index += 1)];
      firstChildRow.display = !firstChildRow.display;

      const endIndex = rows.findIndex((row) => row.depth <= toggleRow.depth);
      rows = endIndex >= 0 ? rows.slice(0, endIndex) : rows;
      rows.forEach((row) => {
        row.display = firstChildRow.display && row.depth === firstChildRow.depth;
      });

      this.currentExpHeaders = Math.max(
        ...this.reportData.rows.filter((r) => r.display).map((r) => r.depth),
        ...this.reportData.sumRow.filter((r) => r.display).map((r) => r.depth)
      );
    }
  }

  canRowCollapse(index: number, sumRow: boolean = false) {
    const rows = sumRow ? this.reportData.sumRow : this.reportData.rows;
    return !rows[index + 1].display;
  }
}
