import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketReportPlannedDateComponent } from './workbasket-report-planned-date.component';

describe('WorkbasketReportPlannedDateComponent', () => {
  let component: WorkbasketReportPlannedDateComponent;
  let fixture: ComponentFixture<WorkbasketReportPlannedDateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WorkbasketReportPlannedDateComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketReportPlannedDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
