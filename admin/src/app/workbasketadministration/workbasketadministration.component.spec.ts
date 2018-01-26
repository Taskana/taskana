import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketadministrationComponent } from './workbasketadministration.component';

describe('WorkbasketadministrationComponent', () => {
  let component: WorkbasketadministrationComponent;
  let fixture: ComponentFixture<WorkbasketadministrationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketadministrationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketadministrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
