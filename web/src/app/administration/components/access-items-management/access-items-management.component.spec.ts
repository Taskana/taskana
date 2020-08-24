import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { AccessItemsManagementComponent } from './access-items-management.component';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { AccessItemsManagementState } from '../../../shared/store/access-items-management-store/access-items-management.state';
import { Observable } from 'rxjs';
import { GetAccessItems } from '../../../shared/store/access-items-management-store/access-items-management.actions';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { TypeaheadModule } from 'ngx-bootstrap';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Direction, Sorting } from '../../../shared/models/sorting';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';

const isFieldValidFn = jest.fn().mockReturnValue(true);
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: isFieldValidFn
  })
);

const showDialogFn = jest.fn().mockReturnValue(true);
const notificationServiceSpy = jest.fn().mockImplementation(
  (): Partial<NotificationService> => ({
    showDialog: showDialogFn
  })
);

describe('AccessItemsManagementComponent', () => {
  let fixture: ComponentFixture<AccessItemsManagementComponent>;
  let debugElement: DebugElement;
  let app: AccessItemsManagementComponent;
  let store: Store;
  let actions$: Observable<any>;

  @Component({ selector: 'taskana-shared-spinner', template: '' })
  class TaskanaSharedSpinnerStub {
    @Input() isRunning: boolean;
  }

  @Component({ selector: 'taskana-shared-sort', template: '' })
  class TaskanaSharedSortStub {
    @Input() sortingFields: Map<string, string>;
    @Output() performSorting = new EventEmitter<Sorting>();
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        NgxsModule.forRoot([EngineConfigurationState, AccessItemsManagementState]),
        FormsModule,
        ReactiveFormsModule,
        AngularSvgIconModule,
        MatSnackBarModule,
        MatDialogModule,
        TypeaheadModule.forRoot(),
        BrowserAnimationsModule
      ],
      declarations: [
        AccessItemsManagementComponent,
        TypeAheadComponent,
        TaskanaSharedSortStub,
        TaskanaSharedSpinnerStub
      ],
      providers: [
        { provide: FormsValidatorService, useClass: formValidatorServiceSpy },
        { provide: NotificationService, useClass: notificationServiceSpy },
        RequestInProgressService,
        ClassificationCategoriesService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AccessItemsManagementComponent);
    debugElement = fixture.debugElement;
    app = fixture.debugElement.componentInstance;
    store = TestBed.inject(Store);
    actions$ = TestBed.inject(Actions);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock
    });
    app.accessIdSelected = '1';
    fixture.detectChanges();
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
    const engineConfigs = store.selectSnapshot((state) => {
      return state.engineConfiguration.customisation.EN.workbaskets['access-items'];
    });
    expect(engineConfigs).toBeDefined();
    expect(engineConfigs).not.toEqual([]);

    const groups = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(groups).toBeDefined();
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
    app.accessId = { accessId: '1', name: 'max' };
    app.groups = [
      { accessId: '1', name: 'users' },
      { accessId: '2', name: 'users' }
    ];
    app.sortModel = { sortBy: 'access-id', sortDirection: 'desc' };
    app.searchForAccessItemsWorkbaskets();
    fixture.detectChanges();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(GetAccessItems)).subscribe(() => {
      actionDispatched = true;
      expect(actionDispatched).toBe(true);
      expect(app.setAccessItemsGroups).toHaveBeenCalled();
      done();
    });
  }));

  it('should display a dialog when access is revoked', async(() => {
    app.accessIdSelected = '';
    const notificationService = TestBed.inject(NotificationService);
    const showDialogSpy = jest.spyOn(notificationService, 'showDialog').mockImplementation();
    app.revokeAccess();
    fixture.detectChanges();
    expect(showDialogSpy).toHaveBeenCalled();
  }));

  it('should create accessItemsForm in setAccessItemsGroups', () => {
    app.setAccessItemsGroups([]);
    expect(app.accessItemsForm).toBeDefined();
    expect(app.accessItemsForm).not.toBeNull();
  });

  it('should invoke sorting function correctly', () => {
    const newSort = new Sorting('access-id', Direction.DESC);
    app.accessId = { accessId: '1', name: 'max' };
    app.groups = [{ accessId: '1', name: 'users' }];
    app.sorting(newSort);
    expect(app.sortModel).toMatchObject(newSort);
  });

  it('should not return accessItemsGroups when accessItemsForm is null', () => {
    app.accessItemsForm = null;
    expect(app.accessItemsGroups).toBeNull();
  });
});
