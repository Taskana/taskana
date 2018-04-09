import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassificationTypesSelectorComponent } from './classification-types-selector.component';

describe('ClassificationTypesSelectorComponent', () => {
  let component: ClassificationTypesSelectorComponent;
  let fixture: ComponentFixture<ClassificationTypesSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationTypesSelectorComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationTypesSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
