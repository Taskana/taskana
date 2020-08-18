import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { Component, DebugElement, ElementRef, EventEmitter, Input, Output } from '@angular/core';
import { Actions, NgxsModule, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ICONTYPES } from '../../../shared/models/icon-types';
import { MapValuesPipe } from '../../../shared/pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../../shared/pipes/remove-empty-type.pipe';
import { AccessIdDefinition } from '../../../shared/models/access-id';
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
@Component({ selector: 'taskana-shared-type-ahead', template: '' })
class TypeAheadStub {
  @Input() placeHolderMessage;
  @Input() validationValue;
  @Input() displayError;
  @Input() width;
  @Input() disable;
  @Input() isRequired = true;
  @Output() selectedItem = new EventEmitter<AccessIdDefinition>();
  @Output() inputField = new EventEmitter<ElementRef>();
}

const triggerWorkbasketSavedFn = jest.fn().mockReturnValue(true);
const workbasketServiceMock = jest.fn().mockImplementation(
  (): Partial<WorkbasketService> => ({
    triggerWorkBasketSaved: triggerWorkbasketSavedFn
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

const selectedWorkbasket = {
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

export const workbasketCopyState = {
  selectedWorkbasket,
  action: ACTION.COPY
};

export const workbasketCreateState = {
  selectedWorkbasket,
  action: ACTION.CREATE
};

export const workbasketReadState = {
  selectedWorkbasket,
  action: ACTION.READ
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
        NgxsModule.forRoot([EngineConfigurationState]),
        NgxsModule.forRoot([WorkbasketState]),
        RouterTestingModule.withRoutes([])
      ],
      declarations: [
        WorkbasketInformationComponent,
        SpinnerStub,
        FieldErrorDisplayStub,
        IconTypeStub,
        TypeAheadStub,
        MapValuesPipe,
        RemoveNoneTypePipe
      ],
      providers: [
        { provide: WorkbasketService, useClass: workbasketServiceMock },
        SavingWorkbasketService,
        RequestInProgressService,
        FormsValidatorService,
        NotificationService,
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
    component.action = ACTION.READ;

    console.log(store.selectSnapshot((state) => state).engineConfiguration.customisation['EN'].workbaskets);
    component.workbasketsCustomisation$.subscribe((value) => {
      console.log(value);
    });
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('', () => {});
});
