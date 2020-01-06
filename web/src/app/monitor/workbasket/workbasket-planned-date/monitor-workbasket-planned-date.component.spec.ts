import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorWorkbasketPlannedDateComponent } from './monitor-workbasket-planned-date.component';

describe('MonitorWorkbasketPlannedDateComponent', () => {
  let component: MonitorWorkbasketPlannedDateComponent;
  let fixture: ComponentFixture<MonitorWorkbasketPlannedDateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MonitorWorkbasketPlannedDateComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitorWorkbasketPlannedDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
