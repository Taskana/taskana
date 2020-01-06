import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskQueryComponent } from './task-query.component';

describe('TaskQueryComponent', () => {
  let component: TaskQueryComponent;
  let fixture: ComponentFixture<TaskQueryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TaskQueryComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
