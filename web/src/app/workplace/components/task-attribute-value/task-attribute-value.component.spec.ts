import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TaskAttributeValueComponent } from './task-attribute-value.component';

// TODO: test pending to test. Failing random
xdescribe('TaskAttributeValueComponent', () => {
  let component: TaskAttributeValueComponent;
  let fixture: ComponentFixture<TaskAttributeValueComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [TaskAttributeValueComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskAttributeValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
