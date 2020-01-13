import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorWorkbasketDueDateComponent } from './monitor-workbasket-due-date.component';

describe('MonitorWorkbasketDueDateComponent', () => {
  let component: MonitorWorkbasketDueDateComponent;
  let fixture: ComponentFixture<MonitorWorkbasketDueDateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MonitorWorkbasketDueDateComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitorWorkbasketDueDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
