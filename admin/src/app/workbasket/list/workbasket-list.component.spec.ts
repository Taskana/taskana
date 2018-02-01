import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketListComponent } from './workbasket-list.component';

describe('WorkbasketListComponent', () => {
  let component: WorkbasketListComponent;
  let fixture: ComponentFixture<WorkbasketListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
