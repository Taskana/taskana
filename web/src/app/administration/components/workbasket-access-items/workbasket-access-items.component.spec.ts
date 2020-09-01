import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketAccessItemsComponent } from './workbasket-access-items.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
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
import {
  workbasketAccessItemsMock,
  engineConfigurationMock,
  selectedWorkbasketMock
} from '../../../shared/store/mock-data/mock-store';
import {
  GetWorkbasketAccessItems,
  UpdateWorkbasketAccessItems
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ACTION } from '../../../shared/models/action';
import { WorkbasketAccessItems } from '../../../shared/models/workbasket-access-items';

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

const showDialogFn = jest.fn().mockReturnValue(true);
const notificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    triggerError: showDialogFn,
    showToast: showDialogFn
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
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule
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
    component.workbasket = { ...selectedWorkbasketMock };
    component.accessItemsRepresentation = workbasketAccessItemsMock;
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock,
      workbasket: {
        workbasketAccessItems: workbasketAccessItemsMock
      }
    });
    fixture.detectChanges();
  }));

  afterEach(async(() => {
    component.workbasket = { ...selectedWorkbasketMock };
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize when accessItems exist', async((done) => {
    component.action = ACTION.COPY;
    let actionDispatched = false;
    component.onSave = jest.fn().mockImplementation();
    actions$.pipe(ofActionDispatched(GetWorkbasketAccessItems)).subscribe(() => (actionDispatched = true));
    component.init();
    expect(component.initialized).toBe(true);
    expect(actionDispatched).toBe(true);
    expect(component.onSave).toHaveBeenCalled();
  }));

  it("should discard initializing when accessItems don't exist", () => {
    component.workbasket._links.accessItems = null;
    component.init();
    expect(component.initialized).toBe(false);
  });

  it('should add accessItems when add access item button is clicked', () => {
    const addAccessItemButton = debugElement.nativeElement.querySelector('button.add-access-item');
    const clearSpy = jest.spyOn(component, 'addAccessItem');
    addAccessItemButton.click();
    expect(addAccessItemButton.title).toMatch('Add new access');
    expect(clearSpy).toHaveBeenCalled();
  });

  it('should undo changes when undo button is clicked', () => {
    const undoButton = debugElement.nativeElement.querySelector('button.undo-button');
    const clearSpy = jest.spyOn(component, 'clear');
    undoButton.click();
    expect(undoButton.title).toMatch('Undo Changes');
    expect(clearSpy).toHaveBeenCalled();
  });

  it('should check all permissions when check all box is checked', () => {
    const checkAllSpy = jest.spyOn(component, 'checkAll');
    const checkAllButton = debugElement.nativeElement.querySelector('#checkbox-0-00');
    checkAllButton.click();
    expect(checkAllSpy).toHaveBeenCalled();
    expect(checkAllButton).toBeTruthy();
  });

  it('should dispatch UpdateWorkbasketAccessItems action when save button is triggered', () => {
    component.accessItemsRepresentation._links.self.href = 'https://link.mock';
    const onSaveSpy = jest.spyOn(component, 'onSave');
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(UpdateWorkbasketAccessItems)).subscribe(() => (actionDispatched = true));
    component.onSave();
    expect(onSaveSpy).toHaveBeenCalled();
    expect(actionDispatched).toBe(true);
  });

  it('should set badge correctly', () => {
    component.action = ACTION.READ;
    component.setBadge();
    expect(component.badgeMessage).toMatch('');

    component.action = ACTION.COPY;
    component.setBadge();
    expect(component.badgeMessage).toMatch(`Copying workbasket: ${component.workbasket.key}`);
  });
});
