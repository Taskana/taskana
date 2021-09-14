import { AfterViewInit, Component, Input } from '@angular/core';
import { Chart } from 'chart.js';
import { priorityTypes } from '../../models/priority';

import { ReportRow } from '../../models/report-row';

@Component({
  selector: 'taskana-monitor-canvas',
  templateUrl: './canvas.component.html',
  styleUrls: ['./canvas.component.scss']
})
export class CanvasComponent implements AfterViewInit {
  @Input() row: ReportRow;
  @Input() id: string;
  @Input() isReversed: boolean;

  ngAfterViewInit() {
    const canvas = document.getElementById(this.id) as HTMLCanvasElement;
    if (canvas && this.id && this.row) {
      this.generateChart(this.id, this.row);
    }
  }

  generateChart(id: string, row: ReportRow) {
    const canvas = document.getElementById(id) as HTMLCanvasElement;
    new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: [priorityTypes.HIGH, priorityTypes.MEDIUM, priorityTypes.LOW],
        datasets: [
          {
            label: 'Tasks by Priority',
            // depends on whether backend sends data sorted in ascending or descending order
            data: this.isReversed ? row.cells.reverse() : row.cells,
            backgroundColor: ['red', 'gold', 'limegreen'],
            borderWidth: 0
          }
        ]
      },
      options: {
        rotation: Math.PI,
        circumference: Math.PI,
        title: {
          display: true,
          text: String(row.total),
          position: 'bottom',
          fontSize: 18
        }
      }
    });
  }
}
