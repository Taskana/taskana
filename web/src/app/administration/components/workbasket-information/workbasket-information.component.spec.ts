import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { Workbasket } from 'app/shared/models/workbasket';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { ACTION } from 'app/shared/models/action';

import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgxsModule, Store } from '@ngxs/store';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {
}

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

  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [WorkbasketInformationComponent, DummyDetailComponent],
      imports: [FormsModule, AngularSvgIconModule, HttpClientModule, RouterTestingModule.withRoutes(routes), NgxsModule.forRoot()],
      providers: [WorkbasketService, NotificationService, SavingWorkbasketService,
        RequestInProgressService, FormsValidatorService, { provide: Store, useValue: storeSpy }]

    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      storeSpy.select.and.callFake(selector => {
        switch (selector) {
          case EngineConfigurationSelectors.workbasketsCustomisation:
            return of({ information: {} });
          default:
            return of();
        }
      });

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
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a panel with heading and form with all fields', async(() => {
    component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
      'modified', 'name', 'description', 'owner', 'custom1', 'custom2', 'custom3', 'custom4',
      'orgLevel1', 'orgLevel2', 'orgLevel3', 'orgLevel4', null);
    fixture.detectChanges();
    expect(debugElement.querySelector('#wb-information')).toBeDefined();
    expect(debugElement.querySelector('#wb-information > .panel-heading > h4').textContent.trim()).toBe('name');
    expect(debugElement.querySelectorAll('#wb-information > .panel-body > form').length).toBe(1);
    fixture.whenStable().then(() => {
      expect(debugElement.querySelector('#wb-information > .panel-body > form > div > div > input ').value).toBe('keyModified');
    });
  }));

  it('selectType should set workbasket.type to personal with 0 and group in other case', () => {
    component.workbasket = new Workbasket('id1');
    expect(component.workbasket.type).toEqual('PERSONAL');
    component.selectType(ICONTYPES.GROUP);
    expect(component.workbasket.type).toEqual('GROUP');
  });

  it('should create a copy of workbasket when workbasket is selected', () => {
    expect(component.workbasketClone).toBeUndefined();
    component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
      'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2', 'orgLevel3', 'orgLevel4');
    component.ngOnChanges(
      undefined
    );
    fixture.detectChanges();
    expect(component.workbasket.workbasketId).toEqual(component.workbasketClone.workbasketId);
  });

  it('should reset requestInProgress after saving request is done', async(() => {
    component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
      'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
      'orgLevel3', 'orgLevel4', { self: { href: 'someUrl' } });
    fixture.detectChanges();
    spyOn(workbasketService, 'updateWorkbasket').and.returnValue(of(component.workbasket));
    spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(of(component.workbasket));
    component.onSubmit();
    expect(component.requestInProgress).toBeFalsy();
  }));

  it('should trigger triggerWorkBasketSaved method after saving request is done', async(() => {
    component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
      'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
      'orgLevel3', 'orgLevel4', { self: { href: 'someurl' } });
    spyOn(workbasketService, 'updateWorkbasket').and.returnValue(of(component.workbasket));
    spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(of(component.workbasket));
    fixture.detectChanges();

    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(workbasketService.triggerWorkBasketSaved).toHaveBeenCalled();
    });
  }));

  it('should post a new workbasket when no workbasketId is defined and update workbasket', async(() => {
    component.workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
      'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
      'orgLevel3', 'orgLevel4', {});
    spyOn(workbasketService, 'createWorkbasket').and.returnValue(of(
      new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
        'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
        'orgLevel3', 'orgLevel4', {})
    ));
    fixture.detectChanges();
    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(alertService.showToast).toHaveBeenCalled();
      expect(component.workbasket.workbasketId).toBe('someNewId');
    });
  }));

  it('should post a new workbasket, new distribution targets and new access '
    + 'items when no workbasketId is defined and action is copy', async(() => {
    component.workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
      'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
      'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
      'orgLevel3', 'orgLevel4', {});
    component.action = ACTION.COPY;

    spyOn(workbasketService, 'createWorkbasket').and.returnValue(of(
      new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
        'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
        'orgLevel3', 'orgLevel4', { distributionTargets: { href: 'someurl' }, accessItems: { href: 'someurl' } })
    ));

    spyOn(savingWorkbasketService, 'triggerDistributionTargetSaving');
    spyOn(savingWorkbasketService, 'triggerAccessItemsSaving');
    fixture.detectChanges();
    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(alertService.showToast).toHaveBeenCalled();
      expect(component.workbasket.workbasketId).toBe('someNewId');
      expect(savingWorkbasketService.triggerDistributionTargetSaving).toHaveBeenCalled();
      expect(savingWorkbasketService.triggerAccessItemsSaving).toHaveBeenCalled();
    });
  }));

  it('should trigger requestInProgress service true before  and requestInProgress false after remove a workbasket', () => {
    component.workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
      'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
      'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
      'orgLevel3', 'orgLevel4', { removeDistributionTargets: { href: 'someurl' } });
    spyOn(workbasketService, 'removeDistributionTarget').and.returnValue(of(undefined));
    const requestInProgressServiceSpy = spyOn(requestInProgressService, 'setRequestInProgress');

    component.removeDistributionTargets();
    expect(requestInProgressServiceSpy).toHaveBeenCalledWith(true);
    workbasketService.removeDistributionTarget().subscribe(() => {

    }, () => { }, () => {
      expect(requestInProgressServiceSpy).toHaveBeenCalledWith(false);
    });
  });
});
