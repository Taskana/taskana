import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { AccessItemsManagementComponent } from './access-items-management.component';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { CUSTOM_ELEMENTS_SCHEMA, DebugElement } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { AccessItemsManagementState } from '../../../shared/store/access-items-management-store/access-items-management.state';
import { Observable, zip } from 'rxjs';
import { GetAccessItems } from '../../../shared/store/access-items-management-store/access-items-management.actions';

const isFieldValidFn = jest.fn().mockReturnValue(true);
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: isFieldValidFn
  })
);

const showDialogFn = jest.fn().mockReturnValue(true);
const NotificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    showDialog: showDialogFn
  })
);

export const engineConfigInitState = {
  customisation: {
    EN: {
      workbaskets: {
        'access-items': {
          accessId: {
            lookupField: true
          },
          custom3: {
            field: '',
            visible: false
          },
          custom9: {
            field: 'Some custom field',
            visible: true
          },
          custom10: {
            field: '',
            visible: false
          },
          custom11: {
            field: '',
            visible: false
          },
          custom12: {
            field: '',
            visible: false
          }
        }
      }
    }
  }
};

describe('AccessItemsManagementComponent', () => {
  let fixture: ComponentFixture<AccessItemsManagementComponent>;
  let debugElement: DebugElement;
  let app: AccessItemsManagementComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        NgxsModule.forRoot([EngineConfigurationState, AccessItemsManagementState]),
        FormsModule,
        ReactiveFormsModule,
        AngularSvgIconModule
      ],
      declarations: [AccessItemsManagementComponent],
      providers: [
        { provide: FormsValidatorService, useClass: formValidatorServiceSpy },
        { provide: NotificationService, useClass: NotificationServiceSpy },
        RequestInProgressService,
        ClassificationCategoriesService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AccessItemsManagementComponent);
    debugElement = fixture.debugElement;
    app = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigInitState
    });
    app.accessId = { accessId: '1', name: '' };
    app.accessIdSelected = '1';
    app.groups = [];
  }));

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it('should display header correctly as Access items management', () => {
    const panelHeader = () => debugElement.nativeElement.querySelector('h4.panel-header').textContent;
    expect(panelHeader()).toBe('Access items management');
  });

  it('should render search type ahead', () => {
    const typeAhead = () => debugElement.nativeElement.querySelector('taskana-shared-type-ahead');
    expect(typeAhead()).toBeTruthy();
  });

  it('should not display result table when search bar is empty', () => {
    const form = () => debugElement.nativeElement.querySelector('ng-form');
    expect(form()).toBeFalsy();
  });

  it('should initialize app with ngxs store', () => {
    store.selectOnce((state) => state).subscribe((state) => expect(state).toBeTruthy);
    const engineConfigs = store.selectSnapshot((state) => {
      console.debug(state.engineConfiguration.customisation.EN.workbaskets['access-items']);
      return state.engineConfiguration.customisation.EN.workbaskets['access-items'];
    });
    expect(engineConfigs).not.toEqual([]);

    const groups = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(groups).toBeTruthy();
  });

  it('should be able to get groups if selected access ID is not null in onSelectAccessId', () => {
    const selectedAccessId = { accessId: '1', name: '' };
    app.onSelectAccessId(selectedAccessId);
    const groups = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(selectedAccessId).not.toBeNull();
    expect(groups).not.toBeNull();
    expect(app.accessItemsForm).not.toBeNull();

    app.onSelectAccessId(null);
    expect(app.accessItemsForm).toBeNull();
  });

  it('should dispatch GetAccessItems action in searchForAccessItemsWorkbaskets', async((done) => {
    app.searchForAccessItemsWorkbaskets();
    let actionDispatched = false;
    zip(actions$.pipe(ofActionDispatched(GetAccessItems))).subscribe(() => {
      actionDispatched = true;
      expect(actionDispatched).toBe(true);
      expect(app.setAccessItemsGroups).toBeCalled();
      done();
    });
  }));

  it('should display a dialog in when access is revoked', async(() => {
    inject([NotificationService], (notificationService: NotificationService) => {
      app.revokeAccess();
      expect(app.revokeAccess).toHaveBeenCalled();
      expect(notificationService.showDialog).toHaveBeenCalled();
    });
  }));

  it('should create accessItemsForm in setAccessItemsGroups', () => {
    app.setAccessItemsGroups([]);
    expect(app.accessItemsForm).toBeTruthy();
    expect(app.accessItemsForm).not.toBeNull();
  });
});
