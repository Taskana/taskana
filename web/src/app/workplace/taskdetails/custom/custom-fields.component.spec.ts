import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsCustomFieldsComponent } from './custom-fields.component';

describe('CustomComponent', () => {
  let component: TaskdetailsCustomFieldsComponent;
  let fixture: ComponentFixture<TaskdetailsCustomFieldsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskdetailsCustomFieldsComponent ]
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
