import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AccessItemsManagementComponent } from './access-items-management.component';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';
import { AccessItemsManagementState } from '../../../shared/store/access-items-management-store/access-items-management.state';
import { Observable } from 'rxjs';
import { GetAccessItems } from '../../../shared/store/access-items-management-store/access-items-management.actions';
import { MatDialogModule } from '@angular/material/dialog';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Direction, Sorting, WorkbasketAccessItemQuerySortParameter } from '../../../shared/models/sorting';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { KadaiEngineService } from '../../../shared/services/kadai-engine/kadai-engine.service';
import { WindowRefService } from '../../../shared/services/window/window.service';
import { engineConfigurationMock } from '../../../shared/store/mock-data/mock-store';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTableModule } from '@angular/material/table';

jest.mock('angular-svg-icon');

const isFieldValidFn = jest.fn().mockReturnValue(true);
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: isFieldValidFn
  })
);

const showDialogFn = jest.fn().mockReturnValue(true);
const notificationServiceSpy: Partial<NotificationService> = {
  showDialog: showDialogFn
};

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {}

describe('AccessItemsManagementComponent', () => {
  let fixture: ComponentFixture<AccessItemsManagementComponent>;
  let debugElement: DebugElement;
  let app: AccessItemsManagementComponent;
  let store: Store;
  let actions$: Observable<any>;

  @Component({ selector: 'kadai-shared-spinner', template: '' })
  class KadaiSharedSpinnerStub {
    @Input() isRunning: boolean;
  }

  @Component({ selector: 'kadai-shared-sort', template: '' })
  class KadaiSharedSortStub {
    @Input() sortingFields: Map<WorkbasketAccessItemQuerySortParameter, string>;
    @Input() defaultSortBy: WorkbasketAccessItemQuerySortParameter;
    @Output() performSorting = new EventEmitter<Sorting<WorkbasketAccessItemQuerySortParameter>>();
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        NgxsModule.forRoot([EngineConfigurationState, AccessItemsManagementState]),
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        TypeaheadModule.forRoot(),
        NoopAnimationsModule,
        MatFormFieldModule,
        MatSelectModule,
        MatAutocompleteModule,
        MatInputModule,
        MatProgressBarModule,
        MatIconModule,
        MatCheckboxModule,
        MatTooltipModule,
        MatDividerModule,
        MatListModule,
        MatExpansionModule,
        MatTableModule
      ],
      declarations: [
        AccessItemsManagementComponent,
        TypeAheadComponent,
        KadaiSharedSortStub,
        KadaiSharedSpinnerStub,
        SvgIconStub
      ],
      providers: [
        { provide: FormsValidatorService, useValue: formValidatorServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        RequestInProgressService,
        ClassificationCategoriesService,
        StartupService,
        KadaiEngineService,
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
    fixture.detectChanges();
  }));

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it('should render search type ahead', () => {
    const typeAhead = () => debugElement.nativeElement.querySelector('kadai-shared-type-ahead');
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

    const permissions = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(permissions).toBeDefined();
  });

  it('should be able to get groups if selected access ID is not null in onSelectAccessId', () => {
    const selectedAccessId = { accessId: '1', name: '' };
    app.onSelectAccessId(selectedAccessId);
    const groups = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(selectedAccessId).not.toBeNull();
    expect(groups).not.toBeNull();
    app.onSelectAccessId(null);
    expect(groups).toMatchObject({});
  });

  it('should be able to get permissions if selected access ID is not null in onSelectAccessId', () => {
    const selectedAccessId = { accessId: '1', name: '' };
    app.permissions = [
      { accessId: '1', name: 'perm' },
      { accessId: '2', name: 'perm' }
    ];
    app.onSelectAccessId(selectedAccessId);
    const permissions = store.selectSnapshot((state) => state.accessItemsManagement);
    expect(selectedAccessId).not.toBeNull();
    expect(permissions).not.toBeNull();
    app.onSelectAccessId(null);
    expect(permissions).toMatchObject({});
  });

  it('should dispatch GetAccessItems action in searchForAccessItemsWorkbaskets', async((done) => {
    app.accessId = { accessId: '1', name: 'max' };
    app.groups = [
      { accessId: '1', name: 'users' },
      { accessId: '2', name: 'users' }
    ];
    app.permissions = [
      { accessId: '1', name: 'perm' },
      { accessId: '2', name: 'perm' }
    ];
    app.sortModel = {
      'sort-by': WorkbasketAccessItemQuerySortParameter.ACCESS_ID,
      order: Direction.DESC
    };
    app.searchForAccessItemsWorkbaskets();
    fixture.detectChanges();
    let actionDispatched = false;
    actions$.pipe(ofActionDispatched(GetAccessItems)).subscribe(() => {
      actionDispatched = true;
      expect(actionDispatched).toBe(true);
      expect(app.setAccessItemsGroups).toHaveBeenCalled();
      expect(app.setAccessItemsPermissions).toHaveBeenCalled();
      done();
    });
  }));

  it('should display a dialog when access is revoked', async(() => {
    app.accessId = { accessId: 'xyz', name: 'xyz' };
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

  it('should create accessItemsForm in setAccessItemsPermissions', () => {
    app.setAccessItemsPermissions([]);
    expect(app.accessItemsForm).toBeDefined();
    expect(app.accessItemsForm).not.toBeNull();
  });

  it('should invoke sorting function correctly', () => {
    const newSort: Sorting<WorkbasketAccessItemQuerySortParameter> = {
      'sort-by': WorkbasketAccessItemQuerySortParameter.ACCESS_ID,
      order: Direction.DESC
    };
    app.accessId = { accessId: '1', name: 'max' };
    app.groups = [{ accessId: '1', name: 'users' }];
    app.permissions = [{ accessId: '1', name: 'perm' }];
    app.sorting(newSort);
    expect(app.sortModel).toMatchObject(newSort);
  });

  it('should not return accessItemsGroups when accessItemsForm is null', () => {
    app.accessItemsForm = null;
    expect(app.accessItemsGroups).toBeNull();
  });

  it('should not return accessItemsPermissions when accessItemsForm is null', () => {
    app.accessItemsForm = null;
    expect(app.accessItemsPermissions).toBeNull();
  });
});
