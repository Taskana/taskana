import { ClassificationTypesSelectorComponent } from './classification-types-selector.component';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NgxsModule, Store } from '@ngxs/store';
import { ClassificationState } from '../../../shared/store/classification-store/classification.state';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { MatRippleModule } from '@angular/material/core';

export const classificationInitState = {
  selectedClassificationType: 'Document',
  classificationTypes: {
    TASK: [],
    DOCUMENT: []
  }
};

const classificationServiceSpy = jest.fn();
const classificationCategoriesServiceSpy = jest.fn();
const domainServiceSpy = jest.fn();

describe('ClassificationTypesSelectorComponent', () => {
  let fixture: ComponentFixture<ClassificationTypesSelectorComponent>;
  let debugElement: DebugElement;
  let app: ClassificationTypesSelectorComponent;
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([ClassificationState]), MatRippleModule],
      declarations: [ClassificationTypesSelectorComponent, MatRadioButton, MatRadioGroup],
      providers: [
        { provide: ClassificationsService, useClass: classificationServiceSpy },
        { provide: ClassificationCategoriesService, useClass: classificationCategoriesServiceSpy },
        { provide: DomainService, useClass: domainServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClassificationTypesSelectorComponent);
    debugElement = fixture.debugElement;
    app = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    store.reset({
      ...store.snapshot(),
      classification: classificationInitState
    });
    fixture.detectChanges();
  }));

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it('should display selected classification type', () => {
    const button = fixture.debugElement.nativeElement.getElementsByClassName('selected-type');
    expect(button[0].textContent.trim()).toBe('Document');
  });

  it('should display list of classification types', () => {
    const radioButtons = fixture.debugElement.nativeElement.getElementsByClassName('classification-types');
    expect(radioButtons.length).toBe(2);
    expect(radioButtons[0].textContent.trim()).toBe('TASK');
    expect(radioButtons[1].textContent.trim()).toBe('DOCUMENT');
  });
});
