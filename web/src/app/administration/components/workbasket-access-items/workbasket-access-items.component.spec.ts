import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketAccessItemsComponent } from './workbasket-access-items.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { TypeaheadModule } from 'ngx-bootstrap';
import { SavingWorkbasketService } from '../../services/saving-workbaskets.service';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { SelectedRouteService } from '../../../shared/services/selected-route/selected-route';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { workbasketAccessItemsMock, engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
  @Input() positionClass: string;
}

const savingWorkbasketServiceSpy = jest.fn().mockImplementation(
  (): Partial<SavingWorkbasketService> => ({
    triggeredAccessItemsSaving: jest.fn().mockReturnValue(of(true))
  })
);

const requestInProgressServiceSpy = jest.fn().mockImplementation(
  (): Partial<RequestInProgressService> => ({
    setRequestInProgress: jest.fn()
  })
);

const validateFormInformationFn = jest.fn().mockImplementation((): Promise<any> => Promise.resolve(true));
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: jest.fn().mockReturnValue(true),
    validateInputOverflow: jest.fn(),
    validateFormInformation: validateFormInformationFn,
    get inputOverflowObservable(): Observable<Map<string, boolean>> {
      return of(new Map<string, boolean>());
    }
  })
);

const showDialogFn = jest.fn().mockReturnValue(true);
const notificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    showToast: showDialogFn
  })
);

describe('WorkbasketAccessItemsComponent', () => {
  let fixture: ComponentFixture<WorkbasketAccessItemsComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketAccessItemsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        TypeaheadModule.forRoot(),
        NgxsModule.forRoot([WorkbasketState, EngineConfigurationState]),
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ],
      declarations: [WorkbasketAccessItemsComponent, TypeAheadComponent, SpinnerStub],
      providers: [
        { provide: SavingWorkbasketService, useClass: savingWorkbasketServiceSpy },
        { provide: RequestInProgressService, useClass: requestInProgressServiceSpy },
        { provide: FormsValidatorService, useClass: formValidatorServiceSpy },
        { provide: NotificationService, useClass: notificationServiceSpy },
        ClassificationCategoriesService,
        WorkbasketService,
        DomainService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketAccessItemsComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock,
      workbasket: {
        workbasketAccessItems: workbasketAccessItemsMock
      }
    });
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
