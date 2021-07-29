import { Component, DebugElement, Input } from '@angular/core';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { Observable, of } from 'rxjs';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { ImportExportService } from '../../services/import-export.service';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { ClassificationState } from '../../../shared/store/classification-store/classification.state';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { classificationStateMock, engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';
import { ClassificationDetailsComponent } from './classification-details.component';
import { FormsModule } from '@angular/forms';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import {
  CopyClassification,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveCreatedClassification,
  SaveModifiedClassification
} from '../../../shared/store/classification-store/classification.actions';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({ selector: 'taskana-shared-field-error-display', template: '' })
class FieldErrorDisplayStub {
  @Input() displayError;
  @Input() validationTrigger;
}

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {
  @Input() src;
}

@Component({ selector: 'input', template: '' })
class InputStub {
  @Input() ngModel;
}

@Component({ selector: 'textarea', template: '' })
class TextareaStub {
  @Input() ngModel;
}

const classificationServiceSpy: Partial<ClassificationsService> = {
  getClassification: jest.fn().mockReturnValue(of()),
  getClassifications: jest.fn().mockReturnValue(of()),
  postClassification: jest.fn().mockReturnValue(of()),
  putClassification: jest.fn().mockReturnValue(of()),
  deleteClassification: jest.fn().mockReturnValue(of())
};
const classificationCategoriesServiceSpy: Partial<ClassificationCategoriesService> = {
  getCustomisation: jest.fn().mockReturnValue(of())
};
const domainServiceSpy: Partial<DomainService> = {
  getSelectedDomainValue: jest.fn().mockReturnValue(of('A')),
  getSelectedDomain: jest.fn().mockReturnValue(of())
};
const getImportingFinishedFn = jest.fn().mockReturnValue(of(true));
const importExportServiceSpy: Partial<ImportExportService> = {
  getImportingFinished: getImportingFinishedFn
};

const requestInProgressServiceSpy: Partial<RequestInProgressService> = {
  setRequestInProgress: jest.fn().mockReturnValue(of()),
  getRequestInProgress: jest.fn().mockReturnValue(of(false))
};

const validateFormInformationFn = jest.fn().mockImplementation((): Promise<any> => Promise.resolve(true));
const formsValidatorServiceSpy: Partial<FormsValidatorService> = {
  isFieldValid: jest.fn().mockReturnValue(true),
  validateInputOverflow: jest.fn(),
  validateFormInformation: validateFormInformationFn,
  get inputOverflowObservable(): Observable<Map<string, boolean>> {
    return of(new Map<string, boolean>());
  }
};

const notificationServiceSpy: Partial<NotificationService> = {
  showError: jest.fn().mockReturnValue(of()),
  showSuccess: jest.fn().mockReturnValue(of()),
  showDialog: jest.fn().mockReturnValue(of())
};

describe('ClassificationDetailsComponent', () => {
  let fixture: ComponentFixture<ClassificationDetailsComponent>;
  let debugElement: DebugElement;
  let component: ClassificationDetailsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgxsModule.forRoot([ClassificationState, EngineConfigurationState]),
        FormsModule,
        MatIconModule,
        MatToolbarModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        MatOptionModule,
        MatSelectModule,
        MatProgressBarModule,
        MatMenuModule,
        MatTooltipModule,
        BrowserAnimationsModule
      ],
      declarations: [ClassificationDetailsComponent, InputStub, FieldErrorDisplayStub, SvgIconStub, TextareaStub],
      providers: [
        { provide: ClassificationsService, useValue: classificationServiceSpy },
        { provide: ClassificationCategoriesService, useValue: classificationCategoriesServiceSpy },
        { provide: DomainService, useValue: domainServiceSpy },
        { provide: ImportExportService, useValue: importExportServiceSpy },
        { provide: RequestInProgressService, useValue: requestInProgressServiceSpy },
        { provide: FormsValidatorService, useValue: formsValidatorServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClassificationDetailsComponent);
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

  it('should trigger onSave() when value exists and onSubmit() is called', async () => {
    component.onSave = jest.fn().mockImplementation();
    await component.onSubmit();
    expect(component.onSave).toHaveBeenCalled();
  });

  it('should show warning when onCopy() is called and isCreatingNewClassification is true', () => {
    component.isCreatingNewClassification = true;
    const notificationService = TestBed.inject(NotificationService);
    const showErrorSpy = jest.spyOn(notificationService, 'showError');
    component.onCopy();
    expect(showErrorSpy).toHaveBeenCalled();
  });

  it('should dispatch action when onCopy() is called and isCreatingNewClassification is false', async () => {
    component.isCreatingNewClassification = false;
    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(CopyClassification)).subscribe(() => (isActionDispatched = true));
    component.onCopy();
    expect(isActionDispatched).toBe(true);
  });

  it('should return icon for category when getCategoryIcon() is called and category exists', (done) => {
    const categoryIcon = component.getCategoryIcon('AUTOMATIC');
    categoryIcon.subscribe((iconPair) => {
      expect(iconPair.left).toBe('assets/icons/categories/automatic.svg');
      expect(iconPair.right).toBe('AUTOMATIC');
      done();
    });
  });

  it('should return icon when getCategoryIcon() is called and category does not exist', (done) => {
    const categoryIcon = component.getCategoryIcon('WATER');
    categoryIcon.subscribe((iconPair) => {
      expect(iconPair.left).toBe('assets/icons/categories/missing-icon.svg');
      done();
    });
  });

  it('should dispatch SaveCreatedClassification action in onSave() when classificationId is undefined', async () => {
    component.classification = {};
    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(SaveCreatedClassification)).subscribe(() => (isActionDispatched = true));
    await component.onSave();
    expect(isActionDispatched).toBe(true);
  });

  it('should dispatch SaveModifiedClassification action in onSave() when classificationId is defined', async () => {
    component.classification = { classificationId: 'ID01' };
    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(SaveModifiedClassification)).subscribe(() => (isActionDispatched = true));
    await component.onSave();
    expect(isActionDispatched).toBe(true);
  });

  it('should dispatch action in removeClassificationConfirmation() when classification and classificationId exist', () => {
    component.classification = { classificationId: 'ID01' };
    const requestInProgressService = TestBed.inject(RequestInProgressService);
    const setRequestInProgressSpy = jest.spyOn(requestInProgressService, 'setRequestInProgress');
    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(RemoveSelectedClassification)).subscribe(() => (isActionDispatched = true));
    component.removeClassificationConfirmation();
    expect(setRequestInProgressSpy).toHaveBeenCalled();
    expect(isActionDispatched).toBe(true);
  });

  /* HTML */

  it('should not show details when spinner is running', () => {
    component.requestInProgress = true;
    component.classification = {};
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('.action-toolbar')).toBeFalsy();
    expect(debugElement.nativeElement.querySelector('.detailed-fields')).toBeFalsy();
  });

  it('should not show details when classification does not exist', () => {
    component.requestInProgress = false;
    component.classification = null;
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('.action-toolbar')).toBeFalsy();
    expect(debugElement.nativeElement.querySelector('.detailed-fields')).toBeFalsy();
  });

  it('should show details when classification exists and spinner is not running', () => {
    expect(debugElement.nativeElement.querySelector('.action-toolbar')).toBeTruthy();
    expect(debugElement.nativeElement.querySelector('.detailed-fields')).toBeTruthy();
  });

  /* HTML: TITLE + ACTION BUTTONS */
  it('should display headline with badge message when a new classification is created', () => {
    component.classification = { name: 'Recommendation', type: 'DOCUMENT' };
    component.isCreatingNewClassification = true;
    fixture.detectChanges();
    const headline = debugElement.nativeElement.querySelector('.action-toolbar__headline');
    expect(headline).toBeTruthy();
    expect(headline.textContent).toContain('Recommendation');
    expect(headline.textContent).toContain('DOCUMENT');
    const badgeMessage = headline.children[1];
    expect(badgeMessage).toBeTruthy();
    expect(badgeMessage.textContent.trim()).toBe('Creating new classification');
  });

  it('should call onSubmit() when button is clicked', async () => {
    const button = debugElement.nativeElement.querySelector('.action-toolbar__save-button');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Save');
    expect(button.textContent).toContain('save');
    component.onSubmit = jest.fn().mockImplementation();
    button.click();
    expect(component.onSubmit).toHaveBeenCalled();
  });

  it('should restore selected classification when button is clicked', async () => {
    const button = debugElement.nativeElement.querySelector('.action-toolbar').children[1].children[1];
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Undo Changes');
    expect(button.textContent).toContain('restore');

    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(RestoreSelectedClassification)).subscribe(() => (isActionDispatched = true));
    button.click();
    expect(isActionDispatched).toBe(true);
  });

  it('should display button to show more actions', () => {
    const button = debugElement.nativeElement.querySelector('#action-toolbar__more-buttons');
    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();
    const buttonsInDropdown = debugElement.queryAll(By.css('.action-toolbar__dropdown'));
    expect(buttonsInDropdown.length).toEqual(3);
  });

  it('should not show delete button when creating or copying a Classification', () => {
    component.classification.classificationId = null;
    const button = debugElement.nativeElement.querySelector('#action-toolbar__more-buttons');
    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();
    const buttonsInDropdown = debugElement.queryAll(By.css('.action-toolbar__dropdown'));
    expect(buttonsInDropdown.length).toEqual(2);
  });

  it('should call onCopy() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('#action-toolbar__more-buttons');
    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();
    const copyButton = debugElement.queryAll(By.css('.action-toolbar__dropdown'))[0];
    expect(copyButton.nativeElement.textContent).toContain('content_copy');
    expect(copyButton.nativeElement.textContent).toContain('Copy');
    component.onCopy = jest.fn().mockImplementation();
    copyButton.nativeElement.click();
    expect(component.onCopy).toHaveBeenCalled();
  });

  it('should call onRemoveClassification() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('#action-toolbar__more-buttons');
    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();
    const deleteButton = debugElement.queryAll(By.css('.action-toolbar__dropdown'))[1];
    expect(deleteButton.nativeElement.textContent).toContain('delete');
    expect(deleteButton.nativeElement.textContent).toContain('Delete');

    const onRemoveClassificationSpy = jest.spyOn(component, 'onRemoveClassification');
    deleteButton.nativeElement.click();
    expect(onRemoveClassificationSpy).toHaveBeenCalled();
    onRemoveClassificationSpy.mockReset();

    const notificationService = TestBed.inject(NotificationService);
    const showDialogSpy = jest.spyOn(notificationService, 'showDialog');
    button.click();
    expect(showDialogSpy).toHaveBeenCalled();
  });

  it('should call onClose() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('#action-toolbar__more-buttons');
    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();
    const closeButton = debugElement.queryAll(By.css('.action-toolbar__dropdown'))[2];
    expect(closeButton.nativeElement.textContent).toContain('close');
    expect(closeButton.nativeElement.textContent).toContain('close');
    component.onCloseClassification = jest.fn().mockImplementation();
    closeButton.nativeElement.click();
    expect(component.onCloseClassification).toHaveBeenCalled();
  });

  /* DETAILED FIELDS */
  it('should display field-error-display component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-shared-field-error-display')).toBeTruthy();
  });

  it('should display form field for key', () => {
    expect(debugElement.nativeElement.querySelector('#classification-key')).toBeTruthy();
  });

  it('should display form field for name', () => {
    expect(debugElement.nativeElement.querySelector('#classification-name')).toBeTruthy();
  });

  it('should display form field for service level', () => {
    expect(debugElement.nativeElement.querySelector('#classification-service-level')).toBeTruthy();
  });

  it('should display form field for priority', () => {
    expect(debugElement.nativeElement.querySelector('#classification-priority')).toBeTruthy();
  });

  it('should display form field for domain', () => {
    expect(debugElement.nativeElement.querySelector('#classification-domain')).toBeTruthy();
  });

  it('should display form field for application entry point', () => {
    expect(debugElement.nativeElement.querySelector('#classification-application-entry-point')).toBeTruthy();
  });

  it('should display form field for description', () => {
    expect(debugElement.nativeElement.querySelector('#classification-description')).toBeTruthy();
  });

  it('should change isValidInDomain when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('.detailed-fields__domain-checkbox-icon').parentNode;
    expect(button).toBeTruthy();
    component.classification.isValidInDomain = false;
    button.click();
    expect(component.classification.isValidInDomain).toBe(true);
    button.click();
    expect(component.classification.isValidInDomain).toBe(false);
  });
});
