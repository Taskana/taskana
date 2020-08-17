import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { AccessItemsManagementComponent } from './access-items-management.component';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import {
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  DebugElement,
  ElementRef,
  EventEmitter,
  Input,
  Output
} from '@angular/core';
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
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { TypeaheadModule } from 'ngx-bootstrap';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Sorting } from '../../../shared/models/sorting';
import { AccessIdDefinition } from '../../../shared/models/access-id';

const isFieldValidFn = jest.fn().mockReturnValue(true);
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: isFieldValidFn
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

  @Component({ selector: 'taskana-shared-type-ahead', template: '' })
  class TaskanaSharedTypeAheadStub {
    @Input() validationValue;
    @Input() displayError;
    @Input() disable;
    @Output() selectedItem = new EventEmitter<AccessIdDefinition>();
    @Output() inputField = new EventEmitter<ElementRef>();
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
      declarations: [AccessItemsManagementComponent, TaskanaSharedTypeAheadStub, TaskanaSharedSortStub],
      providers: [
        { provide: FormsValidatorService, useClass: formValidatorServiceSpy },
        NotificationService,
        RequestInProgressService,
        ClassificationCategoriesService
      ]
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

      // Currently not working
      expect(notificationService.showDialog).toHaveBeenCalledWith(
        `Y1231231ou are going to delete all access related: ${app.accessIdSelected}. Can you confirm this action?`
      );
    });
  }));

  it('should create accessItemsForm in setAccessItemsGroups', () => {
    app.setAccessItemsGroups([]);
    expect(app.accessItemsForm).toBeTruthy();
    expect(app.accessItemsForm).not.toBeNull();
  });
});
