import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { provideMockStore } from '@ngrx/store/testing';
import { ClassificationTypesSelectorComponent } from './classification-types-selector.component';

describe('ClassificationTypesSelectorComponent', () => {
  let component: ClassificationTypesSelectorComponent;
  let fixture: ComponentFixture<ClassificationTypesSelectorComponent>;

  beforeEach(async(() => {
    const initialState = {
      Classification: {
        classificationTypes: ['TASK', 'DOCUMENT'],
        selectedClassificationType: 'DOCUMENT',
        categories: ['EXTERNAL'],
      }
    };
    TestBed.configureTestingModule({
      declarations: [ClassificationTypesSelectorComponent],
      providers: [provideMockStore({ initialState })]
    }).compileComponents();
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
