import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { Component, DebugElement, Input } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ICONTYPES } from '../../../shared/models/icon-types';
import { MapValuesPipe } from '../../../shared/pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../../shared/pipes/remove-empty-type.pipe';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SavingWorkbasketService } from '../../services/saving-workbaskets.service';
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
import { TypeaheadModule } from 'ngx-bootstrap';
import { TypeAheadComponent } from '../../../shared/components/type-ahead/type-ahead.component';
import { Workbasket } from '../../../shared/models/workbasket';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
}

@Component({ selector: 'taskana-shared-field-error-display', template: '' })
class FieldErrorDisplayStub {
  @Input() displayError: boolean;
  @Input() errorMessage: string;
  @Input() validationTrigger: boolean;
}

@Component({ selector: 'taskana-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: ICONTYPES = ICONTYPES.ALL;
  @Input() text: string;
}

const triggerWorkbasketSavedFn = jest.fn().mockReturnValue(true);
const workbasketServiceMock = jest.fn().mockImplementation(
  (): Partial<WorkbasketService> => ({
    triggerWorkBasketSaved: triggerWorkbasketSavedFn,
    updateWorkbasket: jest.fn().mockReturnValue(of(true)),
    markWorkbasketForDeletion: jest.fn().mockReturnValue(of(true)),
    createWorkbasket: jest.fn().mockReturnValue(of({ ...selectedWorkbasket }))
  })
);

export const engineConfigurationMock = {
  customisation: {
    EN: {
      workbaskets: {
        information: {
          owner: {
            lookupField: true
          },
          custom1: {
            field: 'Customized field 1 title',
            visible: true
          },
          custom3: {
            field: '',
            visible: false
          }
        },
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
  },
  language: 'EN'
};

const selectedWorkbasket: Workbasket = {
  workbasketId: 'WBI:000000000000000000000000000000000902',
  key: 'sOrt003',
  name: 'bAsxet2',
  domain: 'DOMAIN_A',
  type: ICONTYPES.TOPIC,
  description: 'Lorem ipsum dolor sit amet.',
  owner: 'Max',
  custom1: '',
  custom2: '',
  custom3: '',
  custom4: '',
  orgLevel1: '',
  orgLevel2: '',
  orgLevel3: '',
  orgLevel4: '',
  markedForDeletion: false,
  created: '2020-08-18T09:14:41.353Z',
  modified: '2020-08-18T09:14:41.353Z',
  _links: {
    self: {
      href: 'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902'
    },
    distributionTargets: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    },
    accessItems: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/workbasketAccessItems'
    },
    allWorkbaskets: {
      href: 'http://localhost:8080/taskana/api/v1/workbaskets'
    },
    removeDistributionTargets: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    }
  }
};

export const workbasketReadState = {
  selectedWorkbasket,
  paginatedWorkbasketsSummary: {
    _links: {
      self: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page=3&page-size=8'
      },
      first: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=1'
      },
      last: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=3'
      },
      prev: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=2'
      }
    },
    workbaskets: [
      {
        workbasketId: 'WBI:100000000000000000000000000000000008',
        key: 'USER-2-1',
        name: 'PPK User 1 KSC 2',
        domain: 'DOMAIN_A',
        type: 'PERSONAL',
        description: 'PPK User 1 KSC 2',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000007',
        key: 'USER-1-2',
        name: 'PPK User 2 KSC 1',
        domain: 'DOMAIN_A',
        type: 'PERSONAL',
        description: 'PPK User 2 KSC 1',
        owner: 'Peter Maier',
        custom1: 'custom1',
        custom2: 'custom2',
        custom3: 'custom3',
        custom4: 'custom4',
        orgLevel1: 'versicherung',
        orgLevel2: 'abteilung',
        orgLevel3: 'projekt',
        orgLevel4: 'team',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000009',
        key: 'USER-2-2',
        name: 'PPK User 2 KSC 2',
        domain: 'DOMAIN_A',
        type: 'PERSONAL',
        description: 'PPK User 2 KSC 2',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000010',
        key: 'TPK_VIP',
        name: 'Themenpostkorb VIP',
        domain: 'DOMAIN_A',
        type: 'TOPIC',
        description: 'Themenpostkorb VIP',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000016',
        key: 'TPK_VIP_2',
        name: 'Themenpostkorb VIP 2',
        domain: 'DOMAIN_A',
        type: 'TOPIC',
        description: 'Themenpostkorb VIP',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      }
    ],
    page: {
      size: 8,
      totalElements: 21,
      totalPages: 3,
      number: 3
    }
  },
  action: ACTION.READ
};

const isFieldValidFn = jest.fn().mockReturnValue(true);
const validateFormInformationFn = jest.fn().mockImplementation((): Promise<any> => Promise.resolve(true));
const formValidatorServiceSpy = jest.fn().mockImplementation(
  (): Partial<FormsValidatorService> => ({
    isFieldValid: isFieldValidFn,
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
    showDialog: showDialogFn,
    showToast: showDialogFn,
    triggerError: showDialogFn
  })
);

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
        BrowserAnimationsModule
      ],
      declarations: [
        WorkbasketInformationComponent,
        SpinnerStub,
        FieldErrorDisplayStub,
        IconTypeStub,
        TypeAheadComponent,
        MapValuesPipe,
        RemoveNoneTypePipe
      ],
      providers: [
        { provide: WorkbasketService, useClass: workbasketServiceMock },
        { provide: FormsValidatorService, useClass: formValidatorServiceSpy },
        { provide: NotificationService, useClass: notificationServiceSpy },
        SavingWorkbasketService,
        RequestInProgressService,
        DomainService,
        SelectedRouteService,
        ClassificationCategoriesService
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
      workbasket: workbasketReadState
    });
    component.workbasket = selectedWorkbasket;

    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  //HTML Tests
  it('', () => {});

  it('should create clone of workbasket when workbasket value changes', () => {
    component.action = ACTION.READ;
    component.ngOnChanges();
    expect(component.workbasketClone).toMatchObject(component.workbasket);
  });

  it('should display create badge message when action is CREATE', () => {
    component.action = ACTION.CREATE;
    component.ngOnChanges();
    expect(component.badgeMessage).toMatch('Creating new workbasket');
  });

  it('should display copy badge message when action is COPY', () => {
    component.action = ACTION.COPY;
    component.ngOnChanges();
    expect(component.badgeMessage).toContain(`Copying workbasket: ${component.workbasket.key}`);
  });

  it('should set type variable in selectType', () => {
    const type = ICONTYPES.GROUP;
    component.selectType(type);
    expect(component.workbasket.type).toMatch(type);
  });

  it('should submit if validatorService is true', () => {
    const formsValidatorService = TestBed.inject(FormsValidatorService);
    component.onSubmit();
    expect(formsValidatorService.formSubmitAttempt).toBe(true);
  });

  it('onUndo', () => {
    component.onUndo();
  });

  it('onSave', () => {
    component.onSave();
  });

  it('postNewWOrkbasket', () => {
    component.action = ACTION.COPY;
    component.postNewWorkbasket();
  });

  it('onRemoveConfirmed', () => {
    component.onRemoveConfirmed();
  });

  it('onSave no id', () => {
    delete component.workbasket.workbasketId;
    component.onSave();
  });
});
