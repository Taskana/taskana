import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorWorkbasketsComponent } from './monitor-workbaskets.component';

describe('MonitorWorkbasketsComponent', () => {
  let component: MonitorWorkbasketsComponent;
  let fixture: ComponentFixture<MonitorWorkbasketsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MonitorWorkbasketsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitorWorkbasketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
