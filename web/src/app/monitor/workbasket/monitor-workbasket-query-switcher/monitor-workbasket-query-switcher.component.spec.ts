import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorWorkbasketQuerySwitcherComponent } from './monitor-workbasket-query-switcher.component';

describe('MonitorWorkbasketQuerySwitcherComponent', () => {
  let component: MonitorWorkbasketQuerySwitcherComponent;
  let fixture: ComponentFixture<MonitorWorkbasketQuerySwitcherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MonitorWorkbasketQuerySwitcherComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitorWorkbasketQuerySwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
