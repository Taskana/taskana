import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { Workbasket } from 'app/models/workbasket';
import { ICONTYPES } from 'app/models/type';
import { ACTION } from 'app/models/action';
import { Links } from 'app/models/links';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { configureTests } from 'app/app.test.configuration';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';

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
	let debugElement, workbasketService, alertService, savingWorkbasketService, requestInProgressService, formsValidatorService;

	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				declarations: [WorkbasketInformationComponent, DummyDetailComponent],
				imports: [FormsModule,
					AngularSvgIconModule,
					HttpClientModule,
					RouterTestingModule.withRoutes(routes)],
				providers: [WorkbasketService, AlertService, SavingWorkbasketService, ErrorModalService, RequestInProgressService,
					CustomFieldsService, FormsValidatorService]

			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(WorkbasketInformationComponent);
			component = fixture.componentInstance;
			debugElement = fixture.debugElement.nativeElement;
			workbasketService = TestBed.get(WorkbasketService);
			alertService = TestBed.get(AlertService);
			savingWorkbasketService = TestBed.get(SavingWorkbasketService);
      requestInProgressService = TestBed.get(RequestInProgressService);

      formsValidatorService = TestBed.get(FormsValidatorService);

			spyOn(alertService, 'triggerAlert');
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
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2', 'orgLevel3', 'orgLevel4', null);
		component.ngOnChanges(
			undefined
		);
		fixture.detectChanges();
		expect(component.workbasket.workbasketId).toEqual(component.workbasketClone.workbasketId);
	});

	it('should reset requestInProgress after saving request is done', () => {
		component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
    fixture.detectChanges();
		spyOn(workbasketService, 'updateWorkbasket').and.returnValue(of(component.workbasket));
		spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(of(component.workbasket));
		component.onSubmit();
		expect(component.requestInProgress).toBeFalsy();

	});

	it('should trigger triggerWorkBasketSaved method after saving request is done', async(() => {
		component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
		spyOn(workbasketService, 'updateWorkbasket').and.returnValue(of(component.workbasket));
    spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(of(component.workbasket));
    fixture.detectChanges();

    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
		  expect(workbasketService.triggerWorkBasketSaved).toHaveBeenCalled();
    })
	}));

	it('should post a new workbasket when no workbasketId is defined and update workbasket', async(() => {
		const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
		component.workbasket = workbasket
		spyOn(workbasketService, 'createWorkbasket').and.returnValue(of(
			new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
				'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
				'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }))));
    fixture.detectChanges();
    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(alertService.triggerAlert).toHaveBeenCalled();
      expect(component.workbasket.workbasketId).toBe('someNewId');
    })
	}));

	it('should post a new workbasket, new distribution targets and new access ' +
		'items when no workbasketId is defined and action is copy', async(() => {
			const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
				'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
				'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
				'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
			component.workbasket = workbasket
			component.action = ACTION.COPY;

			spyOn(workbasketService, 'createWorkbasket').and.returnValue(of(
				new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
					'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
					'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }, { 'href': 'someUrl' }, { 'href': 'someUrl' }))));

			spyOn(savingWorkbasketService, 'triggerDistributionTargetSaving');
			spyOn(savingWorkbasketService, 'triggerAccessItemsSaving');
      fixture.detectChanges();
      spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
      component.onSubmit();
      fixture.whenStable().then(() => {
        fixture.detectChanges();
        expect(alertService.triggerAlert).toHaveBeenCalled();
        expect(component.workbasket.workbasketId).toBe('someNewId');
        expect(savingWorkbasketService.triggerDistributionTargetSaving).toHaveBeenCalled();
        expect(savingWorkbasketService.triggerAccessItemsSaving).toHaveBeenCalled();
      })
		}));

	it('should trigger requestInProgress service true before  and requestInProgress false after remove a workbasket', () => {
		const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
			'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
			'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }, undefined, undefined, undefined, { 'href': 'someUrl' }));
		component.workbasket = workbasket;
		spyOn(workbasketService, 'removeDistributionTarget').and.returnValue(of(''));
		const requestInProgressServiceSpy = spyOn(requestInProgressService, 'setRequestInProgress');

		component.removeDistributionTargets();
		expect(requestInProgressServiceSpy).toHaveBeenCalledWith(true);
		workbasketService.removeDistributionTarget().subscribe(() => {

		}, error => { }, complete => {
			expect(requestInProgressServiceSpy).toHaveBeenCalledWith(false);
		});
	})

});
