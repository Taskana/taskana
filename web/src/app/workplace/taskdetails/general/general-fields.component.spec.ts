import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsGeneralFieldsComponent } from './general-fields.component';

describe('GeneralComponent', () => {
  let component: TaskdetailsGeneralFieldsComponent;
  let fixture: ComponentFixture<TaskdetailsGeneralFieldsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TaskdetailsGeneralFieldsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsGeneralFieldsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
