import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketInformationComponent } from './workbasket-information.component';

describe('InformationComponent', () => {
  let component: WorkbasketInformationComponent;
  let fixture: ComponentFixture<WorkbasketInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
