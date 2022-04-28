import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskDetailsContainerComponent } from './task-details-container.component';

describe.skip('TaskDetailsContainerComponent', () => {
  let component: TaskDetailsContainerComponent;
  let fixture: ComponentFixture<TaskDetailsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TaskDetailsContainerComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
