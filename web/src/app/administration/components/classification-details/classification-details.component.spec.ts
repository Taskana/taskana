import { Component, DebugElement, Input } from '@angular/core';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { Observable, of } from 'rxjs';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { ImportExportService } from '../../services/import-export.service';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { ClassificationState } from '../../../shared/store/classification-store/classification.state';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { classificationStateMock, engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';
import { ClassificationDetailsComponent } from './classification-details.component';
import { FormsModule } from '@angular/forms';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { NumberPickerComponent } from '../../../shared/components/number-picker/number-picker.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import {
  CopyClassification,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveCreatedClassification,
  SaveModifiedClassification
} from '../../../shared/store/classification-store/classification.actions';

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning;
}

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

const classificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<ClassificationsService> => ({
    getClassification: jest.fn().mockReturnValue(of()),
    getClassifications: jest.fn().mockReturnValue(of()),
    postClassification: jest.fn().mockReturnValue(of()),
    putClassification: jest.fn().mockReturnValue(of()),
    deleteClassification: jest.fn().mockReturnValue(of())
  })
);
const classificationCategoriesServiceSpy = jest.fn().mockImplementation(
  (): Partial<ClassificationCategoriesService> => ({
    getCustomisation: jest.fn().mockReturnValue(of())
  })
);
const domainServiceSpy = jest.fn().mockImplementation(
  (): Partial<DomainService> => ({
    getSelectedDomainValue: jest.fn().mockReturnValue(of('A')),
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
    setRequestInProgress: jest.fn().mockReturnValue(of())
  })
);

const validateFormInformationFn = jest.fn().mockImplementation((): Promise<any> => Promise.resolve(true));
const formsValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: jest.fn().mockReturnValue(true),
    validateInputOverflow: jest.fn(),
    validateFormInformation: validateFormInformationFn,
    get inputOverflowObservable(): Observable<Map<string, boolean>> {
      return of(new Map<string, boolean>());
    }
  })
);

const notificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    showToast: jest.fn().mockReturnValue(of()),
    showDialog: jest.fn().mockReturnValue(of()),
    triggerError: jest.fn().mockReturnValue(of())
  })
);

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
        MatSnackBarModule,
        MatDialogModule
      ],
      declarations: [
        ClassificationDetailsComponent,
        SpinnerStub,
        InputStub,
        FieldErrorDisplayStub,
        NumberPickerComponent,
        SvgIconStub,
        TextareaStub
      ],
      providers: [
        { provide: ClassificationsService, useClass: classificationServiceSpy },
        { provide: ClassificationCategoriesService, useClass: classificationCategoriesServiceSpy },
        { provide: DomainService, useClass: domainServiceSpy },
        { provide: ImportExportService, useClass: importExportServiceSpy },
        { provide: RequestInProgressService, useClass: requestInProgressServiceSpy },
        { provide: FormsValidatorService, useClass: formsValidatorServiceSpy },
        { provide: NotificationService, useClass: notificationServiceSpy }
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
    const showToastSpy = jest.spyOn(notificationService, 'showToast');
    component.onCopy();
    expect(showToastSpy).toHaveBeenCalled();
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
      expect(iconPair.name).toBe('assets/icons/categories/automatic.svg');
      expect(iconPair.text).toBe('AUTOMATIC');
      done();
    });
  });

  it('should return icon when getCategoryIcon() is called and category does not exist', (done) => {
    const categoryIcon = component.getCategoryIcon('WATER');
    categoryIcon.subscribe((iconPair) => {
      expect(iconPair.name).toBe('assets/icons/categories/missing-icon.svg');
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

  it('should trigger an error in removeClassificationConfirmation() when classification does not exist', () => {
    component.classification = undefined;
    const notificationService = TestBed.inject(NotificationService);
    const triggerErrorSpy = jest.spyOn(notificationService, 'triggerError');
    component.removeClassificationConfirmation();
    expect(triggerErrorSpy).toHaveBeenCalled();
  });

  it('should trigger an error in removeClassificationConfirmation() when classificationId does not exist', () => {
    component.classification = { key: 'Key01' };
    const notificationService = TestBed.inject(NotificationService);
    const triggerErrorSpy = jest.spyOn(notificationService, 'triggerError');
    component.removeClassificationConfirmation();
    expect(triggerErrorSpy).toHaveBeenCalled();
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
  it('should show spinner component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-shared-spinner')).toBeTruthy();
  });

  it('should not show details when spinner is running', () => {
    component.spinnerIsRunning = true;
    component.classification = {};
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('.classification__menu-bar')).toBeFalsy();
    expect(debugElement.nativeElement.querySelector('.classification__detailed-fields')).toBeFalsy();
  });

  it('should not show details when classification does not exist', () => {
    component.spinnerIsRunning = false;
    component.classification = null;
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('.classification__menu-bar')).toBeFalsy();
    expect(debugElement.nativeElement.querySelector('.classification__detailed-fields')).toBeFalsy();
  });

  it('should show details when classification exists and spinner is not running', () => {
    expect(debugElement.nativeElement.querySelector('.classification__menu-bar')).toBeTruthy();
    expect(debugElement.nativeElement.querySelector('.classification__detailed-fields')).toBeTruthy();
  });

  /* HTML: TITLE + ACTION BUTTONS */
  it('should display headline with badge message when a new classification is created', () => {
    component.classification = { name: 'Recommendation', type: 'DOCUMENT' };
    component.isCreatingNewClassification = true;
    fixture.detectChanges();
    const headline = debugElement.nativeElement.querySelector('.classification__headline');
    expect(headline).toBeTruthy();
    expect(headline.textContent).toContain('Recommendation');
    expect(headline.textContent).toContain('DOCUMENT');
    const badgeMessage = headline.children[0];
    expect(badgeMessage).toBeTruthy();
    expect(badgeMessage.textContent.trim()).toBe('Creating new classification');
  });

  it('should call onSubmit() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('.classification__menu-bar').children[0];
    expect(button).toBeTruthy();
    expect(button.title).toBe('Save');
    component.onSubmit = jest.fn().mockImplementation();
    button.click();
    expect(component.onSubmit).toHaveBeenCalled();
  });

  it('should restore selected classification when button is clicked', async () => {
    const button = debugElement.nativeElement.querySelector('.classification__menu-bar').children[1];
    expect(button).toBeTruthy();
    expect(button.title).toBe('Restore Previous Version');

    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(RestoreSelectedClassification)).subscribe(() => (isActionDispatched = true));
    button.click();
    expect(isActionDispatched).toBe(true);
  });

  it('should call onCopy() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('.classification__menu-bar').children[2];
    expect(button).toBeTruthy();
    expect(button.title).toBe('Copy');
    component.onCopy = jest.fn().mockImplementation();
    button.click();
    expect(component.onCopy).toHaveBeenCalled();
  });

  it('should call onRemoveClassification() when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('.classification__menu-bar').children[3];
    expect(button).toBeTruthy();
    expect(button.title).toBe('Delete');

    const onRemoveClassificationSpy = jest.spyOn(component, 'onRemoveClassification');
    button.click();
    expect(onRemoveClassificationSpy).toHaveBeenCalled();
    onRemoveClassificationSpy.mockReset();

    const notificationService = TestBed.inject(NotificationService);
    const showDialogSpy = jest.spyOn(notificationService, 'showDialog');
    button.click();
    expect(showDialogSpy).toHaveBeenCalled();
  });

  /* DETAILED FIELDS */
  it('should display field-error-display component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-shared-field-error-display')).toBeTruthy();
  });

  it('should display number-picker component', () => {
    expect(debugElement.nativeElement.querySelector('taskana-shared-number-picker')).toBeTruthy();
  });

  it('should select category when button is clicked', () => {
    component.classification.category = 'A';
    // component.getAvailableCategories = jest.fn().mockImplementation((type) => of(['B', 'C']));
    fixture.detectChanges();
    const button = debugElement.nativeElement.querySelector('.detailed-fields__categories');
    expect(button).toBeTruthy();
    button.click();
    expect(component.classification.category).toBe('B');
  });

  it('should change isValidInDomain when button is clicked', () => {
    const button = debugElement.nativeElement.querySelector('.detailed-fields__domain').children[2];
    expect(button).toBeTruthy();
    component.classification.isValidInDomain = false;
    button.click();
    expect(component.classification.isValidInDomain).toBe(true);
    button.click();
    expect(component.classification.isValidInDomain).toBe(false);
  });
});
