import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketReportQuerySwitcherComponent } from './workbasket-report-query-switcher.component';

describe('WorkbasketReportQuerySwitcherComponent', () => {
  let component: WorkbasketReportQuerySwitcherComponent;
  let fixture: ComponentFixture<WorkbasketReportQuerySwitcherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WorkbasketReportQuerySwitcherComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketReportQuerySwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
