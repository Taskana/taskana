import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { Workbasket } from 'app/shared/models/workbasket';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Links } from 'app/shared/models/links';

import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgxsModule } from '@ngxs/store';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {}

const routes: Routes = [
  { path: ':id', component: DummyDetailComponent, outlet: 'detail' },
  { path: 'someNewId', component: DummyDetailComponent }
];

describe('WorkbasketInformationComponent', () => {
  let component: WorkbasketInformationComponent;
  let fixture: ComponentFixture<WorkbasketInformationComponent>;
  let debugElement;
  let workbasketService;
  let alertService;
  let savingWorkbasketService;
  let requestInProgressService;
  let formsValidatorService;

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [WorkbasketInformationComponent, DummyDetailComponent],
      imports: [
        FormsModule,
        AngularSvgIconModule,
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        NgxsModule.forRoot()
      ],
      providers: [
        WorkbasketService,
        NotificationService,
        SavingWorkbasketService,
        RequestInProgressService,
        FormsValidatorService
      ]
    });
  };

  function createWorkbasket(
    workbasketId?,
    created?,
    key?,
    domain?,
    type?,
    modified?,
    name?,
    description?,
    owner?,
    custom1?,
    custom2?,
    custom3?,
    custom4?,
    orgLevel1?,
    orgLevel2?,
    orgLevel3?,
    orgLevel4?,
    _links?: Links,
    markedForDeletion?: boolean
  ) {
    if (!type) {
      // eslint-disable-next-line no-param-reassign
      type = 'PERSONAL';
    }
    const workbasket: Workbasket = {
      workbasketId,
      created,
      key,
      domain,
      type,
      modified,
      name,
      description,
      owner,
      custom1,
      custom2,
      custom3,
      custom4,
      orgLevel1,
      orgLevel2,
      orgLevel3,
      orgLevel4,
      markedForDeletion,
      _links
    };
    return workbasket;
  }

  beforeEach((done) => {
    configureTests(configure).then((testBed) => {
      fixture = testBed.createComponent(WorkbasketInformationComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      workbasketService = testBed.get(WorkbasketService);
      alertService = testBed.get(NotificationService);
      savingWorkbasketService = testBed.get(SavingWorkbasketService);
      requestInProgressService = testBed.get(RequestInProgressService);

      formsValidatorService = testBed.get(FormsValidatorService);

      spyOn(alertService, 'showToast');
      fixture.detectChanges();
      done();
    });
  });

  afterEach(() => {
    fixture.destroy();
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('selectType should set workbasket.type to personal with 0 and group in other case', () => {
    component.workbasket = createWorkbasket('id1');
    expect(component.workbasket.type).toEqual('PERSONAL');
    component.selectType(ICONTYPES.GROUP);
    expect(component.workbasket.type).toEqual('GROUP');
  });

  it('should create a copy of workbasket when workbasket is selected', () => {
    expect(component.workbasketClone).toBeUndefined();
    component.workbasket = createWorkbasket(
      'id',
      'created',
      'keyModified',
      'domain',
      ICONTYPES.TOPIC,
      'modified',
      'name',
      'description',
      'owner',
      'custom1',
      'custom2',
      'custom3',
      'custom4',
      'orgLevel1',
      'orgLevel2',
      'orgLevel3',
      'orgLevel4'
    );
    component.ngOnChanges(undefined);
    fixture.detectChanges();
    expect(component.workbasket.workbasketId).toEqual(component.workbasketClone.workbasketId);
  });
});
