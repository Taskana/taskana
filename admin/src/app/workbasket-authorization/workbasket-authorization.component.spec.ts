import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketAuthorizationComponent } from './workbasket-authorization.component';

describe('WorkbasketAuthorizationComponent', () => {
  let component: WorkbasketAuthorizationComponent;
  let fixture: ComponentFixture<WorkbasketAuthorizationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketAuthorizationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketAuthorizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
