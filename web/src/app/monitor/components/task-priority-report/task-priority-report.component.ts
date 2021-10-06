import { Component, OnInit } from '@angular/core';
import { priorityTypes } from '../../models/priority';
import { ReportData } from '../../models/report-data';
import { MonitorService } from '../../services/monitor.service';
import { take } from 'rxjs/operators';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketType } from '../../../shared/models/workbasket-type';

@Component({
  selector: 'taskana-monitor-task-priority-report',
  templateUrl: './task-priority-report.component.html',
  styleUrls: ['./task-priority-report.component.scss']
})
export class TaskPriorityReportComponent implements OnInit {
  columns: string[] = ['priority', 'number'];
  reportData: ReportData;
  tableDataArray: { priority: string; number: number }[][] = [];
  isReversed = true;

  constructor(private monitorService: MonitorService, private notificationService: NotificationService) {}

  ngOnInit() {
    this.monitorService
      .getTasksByPriorityReport([WorkbasketType.TOPIC])
      .pipe(take(1))
      .subscribe((reportData) => {
        this.reportData = reportData;
        let indexHigh = reportData.meta.header.indexOf('>501');
        let indexMedium = reportData.meta.header.indexOf('250 - 500');
        let indexLow = reportData.meta.header.indexOf('<249');
        if (indexHigh == -1 || indexMedium == -1 || indexLow == -1) {
          this.notificationService.showWarning('REPORT_DATA_WRONG_HEADER');
          indexHigh = 2;
          indexMedium = 1;
          indexLow = 0;
        }
        this.isReversed = indexHigh > indexLow;
        reportData.rows.forEach((row) => {
          this.tableDataArray.push([
            { priority: priorityTypes.HIGH, number: row.cells[indexHigh] },
            { priority: priorityTypes.MEDIUM, number: row.cells[indexMedium] },
            { priority: priorityTypes.LOW, number: row.cells[indexLow] },
            { priority: 'Total', number: row.total }
          ]);
        });
      });
  }

  toString(i: number): string {
    return String(i);
  }
}
