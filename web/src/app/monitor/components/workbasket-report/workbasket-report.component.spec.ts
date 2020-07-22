import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketReportComponent } from './workbasket-report.component';

describe('WorkbasketReportComponent', () => {
  let component: WorkbasketReportComponent;
  let fixture: ComponentFixture<WorkbasketReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WorkbasketReportComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
