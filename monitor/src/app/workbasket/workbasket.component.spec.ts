import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketComponent } from './workbasket.component';

describe('WorkbasketComponent', () => {
  let component: WorkbasketComponent;
  let fixture: ComponentFixture<WorkbasketComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
