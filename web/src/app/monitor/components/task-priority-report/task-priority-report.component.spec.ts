import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Component, DebugElement, Input, Pipe, PipeTransform } from '@angular/core';
import { NgxsModule } from '@ngxs/store';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { TaskPriorityReportComponent } from './task-priority-report.component';
import { MonitorService } from '../../services/monitor.service';
import { of } from 'rxjs';
import { MatTableModule } from '@angular/material/table';
import { priorityTypes } from '../../models/priority';
import { workbasketReportMock, workbasketReportUnexpectedHeaderMock } from './monitor-mock-data';

@Pipe({ name: 'germanTimeFormat' })
class GermanTimeFormatPipe implements PipeTransform {
  transform(value: number): number {
    return value;
  }
}

@Component({ selector: 'taskana-monitor-canvas', template: '' })
class CanvasStub {
  @Input() row;
  @Input() id;
  @Input() isReversed;
}

const monitorServiceSpy: Partial<MonitorService> = {
  getTasksByPriorityReport: jest.fn().mockReturnValue(of(workbasketReportMock))
};

const monitorServiceWithDifferentDataSpy: Partial<MonitorService> = {
  getTasksByPriorityReport: jest.fn().mockReturnValue(of(workbasketReportUnexpectedHeaderMock))
};

const notificationServiceSpy: Partial<NotificationService> = {
  showWarning: jest.fn()
};

describe('TaskPriorityReportComponent', () => {
  let fixture: ComponentFixture<TaskPriorityReportComponent>;
  let debugElement: DebugElement;
  let component: TaskPriorityReportComponent;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [NgxsModule.forRoot([]), MatTableModule],
        declarations: [TaskPriorityReportComponent, GermanTimeFormatPipe, CanvasStub],
        providers: [
          { provide: MonitorService, useValue: monitorServiceSpy },
          { provide: NotificationService, useValue: notificationServiceSpy }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(TaskPriorityReportComponent);
      debugElement = fixture.debugElement;
      component = fixture.debugElement.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show Canvas component for all Workbaskets', () => {
    const canvas = debugElement.nativeElement.querySelectorAll('taskana-monitor-canvas');
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

  it('should set isReserved to true when high priority has a higher index than low priority', () => {
    expect(component.isReversed).toBeTruthy();
  });

  it('should set tableDataArray', () => {
    const expectedTableData = [
      [
        { priority: priorityTypes.HIGH, number: 0 },
        { priority: priorityTypes.MEDIUM, number: 0 },
        { priority: priorityTypes.LOW, number: 5 },
        { priority: 'Total', number: 5 }
      ],
      [
        { priority: priorityTypes.HIGH, number: 2 },
        { priority: priorityTypes.MEDIUM, number: 5 },
        { priority: priorityTypes.LOW, number: 3 },
        { priority: 'Total', number: 10 }
      ]
    ];
    expect(component.tableDataArray).toStrictEqual(expectedTableData);
  });
});

describe('TaskPriorityReportComponent with report data containing an unexpected header', () => {
  let fixture: ComponentFixture<TaskPriorityReportComponent>;
  let debugElement: DebugElement;
  let component: TaskPriorityReportComponent;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [NgxsModule.forRoot([]), MatTableModule],
        declarations: [TaskPriorityReportComponent, GermanTimeFormatPipe, CanvasStub],
        providers: [
          WorkbasketService,
          { provide: MonitorService, useValue: monitorServiceWithDifferentDataSpy },
          { provide: NotificationService, useValue: notificationServiceSpy }
        ]
      }).compileComponents();

      fixture = TestBed.createComponent(TaskPriorityReportComponent);
      debugElement = fixture.debugElement;
      component = fixture.debugElement.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should show warning when actual header does not match the expected header', () => {
    const showWarningSpy = jest.spyOn(notificationServiceSpy, 'showWarning');
    expect(showWarningSpy).toHaveBeenCalledTimes(1);
  });
});
