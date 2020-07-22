import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketReportDueDateComponent } from './workbasket-report-due-date.component';

describe('WorkbasketReportDueDateComponent', () => {
  let component: WorkbasketReportDueDateComponent;
  let fixture: ComponentFixture<WorkbasketReportDueDateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WorkbasketReportDueDateComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketReportDueDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
