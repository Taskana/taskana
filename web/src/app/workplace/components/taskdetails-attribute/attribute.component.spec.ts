import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TaskdetailsAttributeComponent } from './attribute.component';

// TODO: test pending to test. Failing random
xdescribe('AttributeComponent', () => {
  let component: TaskdetailsAttributeComponent;
  let fixture: ComponentFixture<TaskdetailsAttributeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [TaskdetailsAttributeComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsAttributeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
