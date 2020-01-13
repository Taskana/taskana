import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassificationTasksComponent } from './classification-tasks.component';

describe('ClassificationTasksComponent', () => {
  let component: ClassificationTasksComponent;
  let fixture: ComponentFixture<ClassificationTasksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationTasksComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
