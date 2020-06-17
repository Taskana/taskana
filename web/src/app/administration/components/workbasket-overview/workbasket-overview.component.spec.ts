import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketOverviewComponent } from './workbasket-overview.component';

describe('WorkbasketOverviewComponent', () => {
  let component: WorkbasketOverviewComponent;
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
