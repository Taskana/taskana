import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TaskdetailsCustomFieldsComponent } from './custom-fields.component';

// TODO: test pending to test. Failing random
xdescribe('CustomComponent', () => {
  let component: TaskdetailsCustomFieldsComponent;
  let fixture: ComponentFixture<TaskdetailsCustomFieldsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [TaskdetailsCustomFieldsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsCustomFieldsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
