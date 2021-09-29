import { AfterViewChecked, Component, OnDestroy, OnInit } from '@angular/core';
import { ReportData } from '../../models/report-data';
import { MonitorService } from '../../services/monitor.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import { Select } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { SettingsSelectors } from '../../../shared/store/settings-store/settings.selectors';
import { Settings } from '../../../settings/models/settings';
import { mergeMap, take, takeUntil } from 'rxjs/operators';
import { SettingMembers } from '../../../settings/components/Settings/expected-members';

@Component({
  selector: 'taskana-monitor-task-priority-report',
  templateUrl: './task-priority-report.component.html',
  styleUrls: ['./task-priority-report.component.scss']
})
export class TaskPriorityReportComponent implements OnInit, AfterViewChecked, OnDestroy {
  columns: string[] = ['priority', 'number'];
  reportData: ReportData;
  tableDataArray: { priority: string; number: number }[][] = [];
  colorShouldChange = true;
  priority = [];

  nameHighPriority: string;
  nameMediumPriority: string;
  nameLowPriority: string;
  colorHighPriority: string;
  colorMediumPriority: string;
  colorLowPriority: string;

  destroy$ = new Subject<void>();

  @Select(SettingsSelectors.getSettings)
  settings$: Observable<Settings>;

  constructor(private monitorService: MonitorService, private notificationService: NotificationService) {}

  ngOnInit() {
    this.settings$
      .pipe(
        takeUntil(this.destroy$),
        mergeMap((settings) => {
          this.setValuesFromSettings(settings);
          // the order must be high, medium, low because the canvas component defines its labels in this order
          this.priority = [
            settings[SettingMembers.intervalHighPriority],
            settings[SettingMembers.intervalMediumPriority],
            settings[SettingMembers.intervalLowPriority]
          ].map((arr) => ({ lowerBound: arr[0], upperBound: arr[1] }));
          return this.monitorService.getTasksByPriorityReport([WorkbasketType.TOPIC], this.priority);
        })
      )
      .subscribe((reportData) => {
        this.setValuesFromReportData(reportData);
      });
  }

  ngAfterViewChecked() {
    if (this.colorShouldChange) {
      const highPriorityElements = document.getElementsByClassName('task-priority-report__row--high');
      if (highPriorityElements.length > 0) {
        this.colorShouldChange = false;
        this.changeColor();
      }
    }
  }

  setValuesFromSettings(settings: Settings) {
    this.nameHighPriority = settings[SettingMembers.nameHighPriority];
    this.nameMediumPriority = settings[SettingMembers.nameMediumPriority];
    this.nameLowPriority = settings[SettingMembers.nameLowPriority];
    this.colorHighPriority = settings[SettingMembers.colorHighPriority];
    this.colorMediumPriority = settings[SettingMembers.colorMediumPriority];
    this.colorLowPriority = settings[SettingMembers.colorLowPriority];
  }

  setValuesFromReportData(reportData) {
    this.reportData = reportData;

    // the order must be high, medium, low because the canvas component defines its labels in this order
    let indexHigh = 0;
    let indexMedium = 1;
    let indexLow = 2;

    this.tableDataArray = [];
    reportData.rows.forEach((row) => {
      this.tableDataArray.push([
        { priority: this.nameHighPriority, number: row.cells[indexHigh] },
        { priority: this.nameMediumPriority, number: row.cells[indexMedium] },
        { priority: this.nameLowPriority, number: row.cells[indexLow] },
        { priority: 'Total', number: row.total }
      ]);
    });
  }

  changeColor() {
    const highPriorityElements = document.getElementsByClassName('task-priority-report__row--high');
    const mediumPriorityElements = document.getElementsByClassName('task-priority-report__row--medium');
    const lowPriorityElements = document.getElementsByClassName('task-priority-report__row--low');
    this.applyColorOnClasses(highPriorityElements, this.colorHighPriority);
    this.applyColorOnClasses(mediumPriorityElements, this.colorMediumPriority);
    this.applyColorOnClasses(lowPriorityElements, this.colorLowPriority);
  }

  applyColorOnClasses(elements: HTMLCollectionOf<Element>, color: string) {
    for (let i = 0; i < elements.length; i++) {
      (<HTMLElement>elements[i]).style.color = color;
    }
  }

  indexToString(i: number): string {
    return String(i);
  }

  applyFilter(filter: {}) {
    this.monitorService
      .getTasksByPriorityReport([WorkbasketType.TOPIC], this.priority, filter)
      .pipe(take(1))
      .subscribe((reportData) => {
        this.colorShouldChange = true;
        this.setValuesFromReportData(reportData);
        return;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
