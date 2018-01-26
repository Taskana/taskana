import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketDistributiontargetsComponent } from './workbasket-distributiontargets.component';

describe('WorkbasketDistributiontargetsComponent', () => {
  let component: WorkbasketDistributiontargetsComponent;
  let fixture: ComponentFixture<WorkbasketDistributiontargetsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketDistributiontargetsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketDistributiontargetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
