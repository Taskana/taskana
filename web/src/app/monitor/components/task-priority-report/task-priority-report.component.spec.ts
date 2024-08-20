import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Component, DebugElement, Input, Pipe, PipeTransform } from '@angular/core';
import { NgxsModule, Store } from '@ngxs/store';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { TaskPriorityReportComponent } from './task-priority-report.component';
import { MonitorService } from '../../services/monitor.service';
import { of } from 'rxjs';
import { MatTableModule } from '@angular/material/table';
import { workbasketReportMock } from './monitor-mock-data';
import { settingsStateMock } from '../../../shared/store/mock-data/mock-store';
import { SettingsState } from '../../../shared/store/settings-store/settings.state';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDividerModule } from '@angular/material/divider';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Pipe({ name: 'germanTimeFormat' })
class GermanTimeFormatPipe implements PipeTransform {
  transform(value: number): number {
    return value;
  }
}

@Component({ selector: 'kadai-monitor-canvas', template: '' })
class CanvasStub {
  @Input() row;
  @Input() id;
  @Input() isReversed;
}

@Component({ selector: 'kadai-monitor-task-priority-report-filter', template: '' })
class TaskPriorityReportFilterStub {}

const monitorServiceSpy: Partial<MonitorService> = {
  getTasksByPriorityReport: jest.fn().mockReturnValue(of(workbasketReportMock))
};

const notificationServiceSpy: Partial<NotificationService> = {
  showWarning: jest.fn()
};

describe('TaskPriorityReportComponent', () => {
  let fixture: ComponentFixture<TaskPriorityReportComponent>;
  let debugElement: DebugElement;
  let component: TaskPriorityReportComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([SettingsState]), MatTableModule, HttpClientTestingModule, MatDividerModule],
      declarations: [TaskPriorityReportComponent, GermanTimeFormatPipe, CanvasStub, TaskPriorityReportFilterStub],
      providers: [
        RequestInProgressService,
        { provide: MonitorService, useValue: monitorServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskPriorityReportComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    const store: Store = TestBed.inject(Store);
    store.reset({
      ...store.snapshot(),
      settings: settingsStateMock
    });
    fixture.detectChanges();
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show Canvas component for all Workbaskets', () => {
    const canvas = debugElement.nativeElement.querySelectorAll('kadai-monitor-canvas');
    expect(canvas).toHaveLength(2);
  });

  it('should show table for all Workbaskets', () => {
    const table = debugElement.nativeElement.querySelectorAll('table');
    expect(table).toHaveLength(2);
  });

  it('should not show warning when actual header matches the expected header', () => {
    const showWarningSpy = jest.spyOn(notificationServiceSpy, 'showWarning');
    component.ngOnInit();
    expect(showWarningSpy).toHaveBeenCalledTimes(0);
  });
});
