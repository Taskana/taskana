import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NgxsModule, Store } from '@ngxs/store';
import { CanvasComponent } from './canvas.component';
import { workbasketReportMock } from '../task-priority-report/monitor-mock-data';
import { SettingsState } from '../../../shared/store/settings-store/settings.state';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { settingsStateMock } from '../../../shared/store/mock-data/mock-store';
import { MatDialogModule } from '@angular/material/dialog';

describe('CanvasComponent', () => {
  let fixture: ComponentFixture<CanvasComponent>;
  let debugElement: DebugElement;
  let component: CanvasComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([SettingsState]), HttpClientTestingModule, MatDialogModule],
      declarations: [CanvasComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CanvasComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    component.id = '1';
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

  it('should show canvas with id from input', () => {
    const id = debugElement.nativeElement.querySelector("[id='1']");
    expect(id).toBeTruthy();
  });

  it('should call generateChart()', () => {
    component.generateChart = jest.fn();
    const reportRow = workbasketReportMock.rows[1];
    component.row = reportRow;
    fixture.detectChanges();
    component.ngAfterViewInit();
    expect(component.generateChart).toHaveBeenCalledWith('1', reportRow);
  });
});
