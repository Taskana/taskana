import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketlistComponent } from './workbasketlist.component';

describe('WorkbasketlistComponent', () => {
  let component: WorkbasketlistComponent;
  let fixture: ComponentFixture<WorkbasketlistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketlistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketlistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
