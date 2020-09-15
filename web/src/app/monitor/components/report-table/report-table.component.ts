import { Component, Input } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';

@Component({
  selector: 'taskana-monitor-report-table',
  templateUrl: './report-table.component.html',
  styleUrls: ['./report-table.component.scss']
})
export class ReportTableComponent {
  currentExpHeaders = 0;

  @Input()
  reportData: ReportData;

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
