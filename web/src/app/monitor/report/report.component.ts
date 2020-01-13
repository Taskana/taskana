import { Component, Input, OnInit } from '@angular/core';
import { ReportData } from 'app/monitor/models/report-data';

@Component({
  selector: 'taskana-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {
  currentExpHeaders = 0;

  @Input()
  reportData: ReportData;

  constructor() {
  }

  ngOnInit(): void {
  }

  toggleFold(index: number, sumRow: boolean = false) {
    const rows = sumRow ? this.reportData.sumRow : this.reportData.rows;
    const toggleRow = rows[index++];
    if (toggleRow.depth < this.reportData.meta.rowDesc.length - 1) {
      const firstChildRow = rows[index++];
      firstChildRow.display = !firstChildRow.display;

      let end = false;
      for (let i = index; i < rows.length && !end; i++) {
        const row = rows[i];
        end = row.depth <= toggleRow.depth;
        if (!end) {
          row.display = firstChildRow.display && row.depth === firstChildRow.depth;
        }
      }
      this.currentExpHeaders = Math.max(
        ...this.reportData.rows.filter(r => r.display).map(r => r.depth),
        ...this.reportData.sumRow.filter(r => r.display).map(r => r.depth)
      );
    }
  }

  canRowCollapse(index: number, sumRow: boolean = false) {
    const rows = sumRow ? this.reportData.sumRow : this.reportData.rows;
    return !rows[index + 1].display;
  }
}
