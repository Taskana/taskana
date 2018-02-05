import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketDetailsComponent } from './workbasket-details.component';

describe('WorkbasketDetailsComponent', () => {
  let component: WorkbasketDetailsComponent;
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
