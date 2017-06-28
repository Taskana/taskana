import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsComponent } from './taskdetails.component';

describe('TaskdetailsComponent', () => {
  let component: TaskdetailsComponent;
  let fixture: ComponentFixture<TaskdetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskdetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
