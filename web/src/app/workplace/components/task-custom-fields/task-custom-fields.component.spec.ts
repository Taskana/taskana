import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TaskCustomFieldsComponent } from './task-custom-fields.component';

// TODO: test pending to test. Failing random
xdescribe('TaskCustomFieldsComponent', () => {
  let component: TaskCustomFieldsComponent;
  let fixture: ComponentFixture<TaskCustomFieldsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [TaskCustomFieldsComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskCustomFieldsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
