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
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { By } from '@angular/platform-browser';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({ selector: 'taskana-administration-import-export', template: '' })
class ImportExportStub {
  @Input() currentSelection: TaskanaType;
  @Input() parentComponent: string;
}

@Component({ selector: 'taskana-administration-classification-types-selector', template: '' })
class ClassificationTypesSelectorStub {}

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

const requestInProgressServiceSpy = jest.fn().mockImplementation(
  (): Partial<RequestInProgressService> => ({
    setRequestInProgress: jest.fn().mockReturnValue(of()),
    getRequestInProgress: jest.fn().mockReturnValue(of(false))
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
      imports: [
        NgxsModule.forRoot([ClassificationState, EngineConfigurationState]),
        MatIconModule,
        MatMenuModule,
        MatFormFieldModule,
        MatInputModule,
        BrowserAnimationsModule,
        MatProgressBarModule
      ],
      declarations: [
        ClassificationListComponent,
        ClassificationTypesSelectorStub,
        TreeStub,
        SvgIconStub,
        ImportExportStub,
        InputStub
      ],
      providers: [
        { provide: ClassificationsService, useClass: classificationServiceSpy },
        { provide: ClassificationCategoriesService, useClass: classificationCategoriesServiceSpy },
        { provide: DomainService, useClass: domainServiceSpy },
        { provide: ImportExportService, useClass: importExportServiceSpy },
        { provide: RequestInProgressService, useClass: requestInProgressServiceSpy }
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
    const button = debugElement.nativeElement.querySelector('.classification-list__add-button');
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
  it('should display filter input field', () => {
    const button = debugElement.nativeElement.querySelector('.classification-list__input-field');
    expect(button).toBeTruthy();
    expect(button.textContent).toBe('Filter classification');
  });

  it('should display filter button', () => {
    const button = debugElement.nativeElement.querySelector('.classification-list__filter-button');
    expect(button).toBeTruthy();
    expect(button.textContent).toBe('filter_list');
  });

  it('should change selectedCategory property when button is clicked', () => {
    const filterButton = debugElement.nativeElement.querySelector('.classification-list__filter-button');
    filterButton.click();
    fixture.detectChanges();
    component.selectedCategory = 'EXTERNAL';
    const allButton = debugElement.query(By.css('.classification-list__all-button'));
    expect(allButton).toBeTruthy();
    allButton.nativeElement.click();
    expect(component.selectedCategory).toBe('');
  });

  it('should display list of categories which can be selected', () => {
    const filterButton = debugElement.nativeElement.querySelector('.classification-list__filter-button');
    filterButton.click();
    fixture.detectChanges();
    const matMenu = debugElement.queryAll(By.css('.classification-list__categories'));
    expect(matMenu.length).toBe(4);
  });

  /* HTML: CLASSIFICATION TREE */
  it('should display tree component when classifications exist', () => {
    component.classifications = [{ classificationId: '1' }, { classificationId: '2' }];
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('taskana-administration-tree')).toBeTruthy();
  });

  it('should display icon and text when no classifications exist', () => {
    const noClassifications = debugElement.nativeElement.querySelector('.classification-list__no-items');
    expect(noClassifications.childNodes.length).toBe(1);
    expect(noClassifications.childNodes[0].textContent).toBe('There are no classifications');
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
