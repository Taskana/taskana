import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { ClassificationState } from '../../../shared/store/classification-store/classification.state';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { ClassificationListComponent } from './classification-list.component';
import { classificationStateMock, engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';
import { TaskanaType } from '../../../shared/models/taskana-type';
import { ImportExportService } from '../../services/import-export.service';
import { Observable, of } from 'rxjs';
import { CreateClassification } from '../../../shared/store/classification-store/classification.actions';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';

@Component({ selector: 'taskana-administration-import-export', template: '' })
class ImportExportStub {
  @Input() currentSelection: TaskanaType;
}

@Component({ selector: 'taskana-administration-classification-types-selector', template: '' })
class ClassificationTypesSelectorStub {}

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning;
}

@Component({ selector: 'taskana-administration-tree', template: '' })
class TreeStub {
  @Input() filterText;
  @Input() filterIcon;
  @Output() switchTaskanaSpinnerEmit = new EventEmitter();
}

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {
  @Input() src;
}

@Component({ selector: 'input', template: '' })
class InputStub {
  @Input() ngModel;
}

const classificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<ClassificationsService> => ({
    getClassification: jest.fn().mockReturnValue(of()),
    getClassifications: jest.fn().mockReturnValue(of())
  })
);
const classificationCategoriesServiceSpy = jest.fn().mockImplementation(
  (): Partial<ClassificationCategoriesService> => ({
    getCustomisation: jest.fn().mockReturnValue(of())
  })
);
const domainServiceSpy = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getSelectedDomainValue: jest.fn().mockReturnValue(of()),
    getSelectedDomain: jest.fn().mockReturnValue(of())
  })
);
const getImportingFinishedFn = jest.fn().mockReturnValue(of(true));
const importExportServiceSpy = jest.fn().mockImplementation(
  (): Partial<ImportExportService> => ({
    getImportingFinished: getImportingFinishedFn
  })
);

describe('ClassificationListComponent', () => {
  let fixture: ComponentFixture<ClassificationListComponent>;
  let debugElement: DebugElement;
  let component: ClassificationListComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([ClassificationState, EngineConfigurationState])],
      declarations: [
        ClassificationListComponent,
        ClassificationTypesSelectorStub,
        SpinnerStub,
        TreeStub,
        SvgIconStub,
        ImportExportStub,
        InputStub
      ],
      providers: [
        { provide: ClassificationsService, useClass: classificationServiceSpy },
        { provide: ClassificationCategoriesService, useClass: classificationCategoriesServiceSpy },
        { provide: DomainService, useClass: domainServiceSpy },
        { provide: ImportExportService, useClass: importExportServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClassificationListComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      classification: classificationStateMock,
      engineConfiguration: engineConfigurationMock
    });
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  /* HTML: ACTION TOOLBAR */
  it('should call CreateClassification when add-classification button is clicked', async () => {
    const button = debugElement.nativeElement.querySelector('.add-classification-button');
    expect(button).toBeTruthy();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(CreateClassification)).subscribe(() => (actionDispatched = true));
    button.click();
    expect(actionDispatched).toBe(true);
  });

  it('should display import-export component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-administration-import-export')).toBeTruthy();
  });

  it('should display classification-types-selector component', () => {
    const typesSelectorComponent = debugElement.nativeElement.querySelector(
      'taskana-administration-classification-types-selector'
    );
    expect(typesSelectorComponent).toBeTruthy();
  });

  /* HTML: FILTER */
  it('should display specific icon when selectedCategory is true', () => {
    component.selectedCategory = 'EXTERNAL';
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('.selected-category')).toBeTruthy();
  });

  it('should display universal icon for categories when selectedCategory is false', () => {
    expect(debugElement.nativeElement.querySelector('svg-icon.no-selected-category')).toBeTruthy();
  });

  it('should change selectedCategory property when button is clicked', () => {
    component.selectedCategory = 'EXTERNAL';
    const button = debugElement.nativeElement.querySelector('.category-all');
    button.click();
    expect(component.selectedCategory).toBe('');
  });

  it('should display list of categories which can be selected', () => {
    expect(debugElement.nativeElement.querySelector('.category-all').textContent.trim()).toBe('All');

    const categories = fixture.debugElement.nativeElement.getElementsByClassName('category-list');
    expect(categories.length).toBe(3);
    expect(categories[0].textContent.trim()).toBe('EXTERNAL');
    expect(categories[1].textContent.trim()).toBe('MANUAL');
    expect(categories[2].textContent.trim()).toBe('AUTOMATIC');
  });

  /* HTML: CLASSIFICATION TREE */
  it('should display spinner component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-shared-spinner')).toBeTruthy();
  });

  it('should display tree component when classifications exist', () => {
    component.classifications = [{ classificationId: '1' }, { classificationId: '2' }];
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('taskana-administration-tree')).toBeTruthy();
  });

  it('should display icon and text when no classifications exist', () => {
    const noClassifications = debugElement.nativeElement.querySelector('.no-classifications');
    expect(noClassifications.childNodes.length).toBe(2);
    expect(noClassifications.childNodes[0].textContent).toBe('There are no classifications');
    expect(noClassifications.childNodes[1].tagName).toBe('SVG-ICON');
  });

  /* TS: getCategoryIcon() */
  it('should return icon for category when getCategoryIcon is called and category exists', (done) => {
    const categoryIcon = component.getCategoryIcon('MANUAL');
    categoryIcon.subscribe((iconPair) => {
      expect(iconPair.name).toBe('assets/icons/categories/manual.svg');
      expect(iconPair.text).toBe('MANUAL');
      done();
    });
  });

  it('should return a special icon when getCategoryIcon is called and category does not exist', (done) => {
    const categoryIcon = component.getCategoryIcon('CLOUD');
    categoryIcon.subscribe((iconPair) => {
      expect(iconPair.name).toBe('assets/icons/categories/missing-icon.svg');
      done();
    });
  });
});
