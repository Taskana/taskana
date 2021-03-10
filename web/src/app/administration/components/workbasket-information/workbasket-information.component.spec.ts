import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import { MapValuesPipe } from '../../../shared/pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../../shared/pipes/remove-empty-type.pipe';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { SelectedRouteService } from '../../../shared/services/selected-route/selected-route';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { ACTION } from '../../../shared/models/action';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MarkWorkbasketForDeletion, UpdateWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import {
  selectedWorkbasketMock,
  engineConfigurationMock,
  workbasketReadStateMock
} from '../../../shared/store/mock-data/mock-store';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({ selector: 'taskana-shared-field-error-display', template: '' })
class FieldErrorDisplayStub {
  @Input() displayError: boolean;
  @Input() errorMessage: string;
  @Input() validationTrigger: boolean;
}

@Component({ selector: 'taskana-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: WorkbasketType;
  @Input() text: string;
}

const triggerWorkbasketSavedFn = jest.fn().mockReturnValue(true);
const workbasketServiceMock: Partial<WorkbasketService> = {
  triggerWorkBasketSaved: triggerWorkbasketSavedFn,
  updateWorkbasket: jest.fn().mockReturnValue(of(true)),
  markWorkbasketForDeletion: jest.fn().mockReturnValue(of(true)),
  createWorkbasket: jest.fn().mockReturnValue(of({ ...selectedWorkbasketMock })),
  getWorkBasket: jest.fn().mockReturnValue(of({ ...selectedWorkbasketMock })),
  getWorkBasketAccessItems: jest.fn().mockReturnValue(of()),
  getWorkBasketsDistributionTargets: jest.fn().mockReturnValue(of())
};

const isFieldValidFn = jest.fn().mockReturnValue(true);
const validateFormInformationFn = jest.fn().mockImplementation((): Promise<any> => Promise.resolve(true));
const formValidatorServiceMock: Partial<FormsValidatorService> = {
  isFieldValid: isFieldValidFn,
  validateInputOverflow: jest.fn(),
  validateFormInformation: validateFormInformationFn,
  get inputOverflowObservable(): Observable<Map<string, boolean>> {
    return of(new Map<string, boolean>());
  }
};

const showDialogFn = jest.fn().mockReturnValue(true);
const notificationServiceMock: Partial<NotificationService> = {
  showDialog: showDialogFn,
  showToast: showDialogFn,
  triggerError: showDialogFn
};

describe('WorkbasketInformationComponent', () => {
  let fixture: ComponentFixture<WorkbasketInformationComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketInformationComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        MatDialogModule,
        NgxsModule.forRoot([EngineConfigurationState, WorkbasketState]),
        TypeaheadModule.forRoot(),
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule,
        MatProgressBarModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatAutocompleteModule,
        MatTooltipModule
      ],
      declarations: [
        WorkbasketInformationComponent,
        FieldErrorDisplayStub,
        IconTypeStub,
        TypeAheadComponent,
        MapValuesPipe,
        RemoveNoneTypePipe
      ],
      providers: [
        { provide: WorkbasketService, useValue: workbasketServiceMock },
        { provide: FormsValidatorService, useValue: formValidatorServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        RequestInProgressService,
        DomainService,
        SelectedRouteService,
        ClassificationCategoriesService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketInformationComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock,
      workbasket: workbasketReadStateMock
    });
    component.workbasket = selectedWorkbasketMock;

    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should display custom fields correctly', () => {
    const customFields = debugElement.nativeElement.getElementsByClassName('custom-fields__form-field');
    expect(customFields.length).toBe(3); //mock data has custom1->4 but engineConfig disables custom3 -> [1,2,4]
  });

  it('should create clone of workbasket when workbasket value changes', () => {
    component.action = ACTION.READ;
    component.ngOnChanges();
    expect(component.workbasketClone).toMatchObject(component.workbasket);
  });

  it('should submit when validatorService is true', () => {
    const formsValidatorService = TestBed.inject(FormsValidatorService);
    component.onSubmit();
    expect(formsValidatorService.formSubmitAttempt).toBe(true);
  });

  it('should reset workbasket information when onUndo is called', () => {
    component.workbasketClone = selectedWorkbasketMock;
    const notificationService = TestBed.inject(NotificationService);
    const toastSpy = jest.spyOn(notificationService, 'showToast');
    component.onUndo();
    expect(toastSpy).toHaveBeenCalled();
    expect(component.workbasket).toMatchObject(component.workbasketClone);
  });

  it('should save workbasket when workbasketId there', async(() => {
    component.workbasket = { ...selectedWorkbasketMock };
    component.workbasket.workbasketId = '1';
    component.action = ACTION.COPY;
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(UpdateWorkbasket)).subscribe(() => (actionDispatched = true));
    component.onSave();
    expect(actionDispatched).toBe(true);
    expect(component.workbasketClone).toMatchObject(component.workbasket);
  }));

  it('should dispatch MarkWorkbasketforDeletion action when onRemoveConfirmed is called', async(() => {
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(MarkWorkbasketForDeletion)).subscribe(() => (actionDispatched = true));
    component.onRemoveConfirmed();
    expect(actionDispatched).toBe(true);
  }));

  it('should create new workbasket when workbasketId is undefined', () => {
    component.workbasket.workbasketId = undefined;
    const postNewWorkbasketSpy = jest.spyOn(component, 'postNewWorkbasket');
    component.onSave();
    expect(postNewWorkbasketSpy).toHaveBeenCalled();
  });
});
