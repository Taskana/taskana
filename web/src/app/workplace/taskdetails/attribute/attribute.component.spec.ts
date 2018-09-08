import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsAttributeComponent } from './attribute.component';

describe('AttributeComponent', () => {
  let component: TaskdetailsAttributeComponent;
  let fixture: ComponentFixture<TaskdetailsAttributeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskdetailsAttributeComponent ]
    })
    .compileComponents();
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
