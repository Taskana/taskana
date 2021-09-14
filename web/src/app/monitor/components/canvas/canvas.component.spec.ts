import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NgxsModule } from '@ngxs/store';
import { CanvasComponent } from './canvas.component';
import { workbasketReportMock } from '../task-priority-report/monitor-mock-data';

describe('CanvasComponent', () => {
  let fixture: ComponentFixture<CanvasComponent>;
  let debugElement: DebugElement;
  let component: CanvasComponent;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [NgxsModule.forRoot([])],
        declarations: [CanvasComponent]
      }).compileComponents();

      fixture = TestBed.createComponent(CanvasComponent);
      debugElement = fixture.debugElement;
      component = fixture.debugElement.componentInstance;
      component.id = '1';
      fixture.detectChanges();
    })
  );

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
