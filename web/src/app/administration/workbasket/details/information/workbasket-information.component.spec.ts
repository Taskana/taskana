import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { WorkbasketService } from 'app/administration/services/workbasket/workbasket.service';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { FormsModule, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs/Observable';
import { Component, Input, forwardRef } from '@angular/core';
import { Routes } from '@angular/router';
import { AppModule } from 'app/app.module'

import { Workbasket } from 'app/models/workbasket';
import { ICONTYPES } from 'app/models/type';
import { ACTION } from 'app/models/action';
import { Links } from 'app/models/links';

import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { MapValuesPipe } from 'app/shared/pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from 'app/shared/pipes/removeNoneType/remove-none-type.pipe';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { DomainService } from 'app/services/domain/domain.service';
import { DomainServiceMock } from 'app/services/domain/domain.service.mock';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
export class DummyDetailComponent {
}

@Component({
	selector: 'taskana-type-ahead',
	template: 'dummydetail',
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			multi: true,
			useExisting: forwardRef(() => TaskanaTypeAheadComponent),
		}
	]
})
export class TaskanaTypeAheadComponent implements ControlValueAccessor {
	@Input()
	placeHolderMessage;

	writeValue(obj: any): void {

	}
	registerOnChange(fn: any): void {

	}
	registerOnTouched(fn: any): void {

	}
	setDisabledState?(isDisabled: boolean): void {

	}

}

const routes: Routes = [
	{ path: ':id', component: DummyDetailComponent, outlet: 'detail' },
	{ path: 'someNewId', component: DummyDetailComponent }
];

describe('InformationComponent', () => {
	let component: WorkbasketInformationComponent;
	let fixture: ComponentFixture<WorkbasketInformationComponent>;
	let debugElement, workbasketService, alertService, savingWorkbasketService, requestInProgressService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [WorkbasketInformationComponent, IconTypeComponent, MapValuesPipe,
				RemoveNoneTypePipe, SpinnerComponent, GeneralMessageModalComponent, DummyDetailComponent,
				TaskanaTypeAheadComponent],
			imports: [FormsModule,
				AngularSvgIconModule,
				HttpClientModule,
				HttpModule,
				RouterTestingModule.withRoutes(routes)],
			providers: [WorkbasketService, AlertService, SavingWorkbasketService, ErrorModalService, RequestInProgressService,
				{
					provide: DomainService,
					useClass: DomainServiceMock
				},
				CustomFieldsService]

		})
			.compileComponents();
		fixture = TestBed.createComponent(WorkbasketInformationComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
		workbasketService = TestBed.get(WorkbasketService);
		alertService = TestBed.get(AlertService);
		savingWorkbasketService = TestBed.get(SavingWorkbasketService);
		requestInProgressService = TestBed.get(RequestInProgressService);

		spyOn(alertService, 'triggerAlert');
	}));

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

	it('should reset requestInProgress after saving request is done', fakeAsync(() => {
		component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
		spyOn(workbasketService, 'updateWorkbasket').and.returnValue(Observable.of(component.workbasket));
		spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(Observable.of(component.workbasket));
		component.onSave();
		expect(component.requestInProgress).toBeFalsy();

	}));

	it('should trigger triggerWorkBasketSaved method after saving request is done', () => {
		component.workbasket = new Workbasket('id', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
		spyOn(workbasketService, 'updateWorkbasket').and.returnValue(Observable.of(component.workbasket));
		spyOn(workbasketService, 'triggerWorkBasketSaved').and.returnValue(Observable.of(component.workbasket));
		component.onSave();
		expect(workbasketService.triggerWorkBasketSaved).toHaveBeenCalled();
	});

	it('should post a new workbasket when no workbasketId is defined and update workbasket', () => {
		const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
			'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
		component.workbasket = workbasket
		spyOn(workbasketService, 'createWorkbasket').and.returnValue(Observable.of(
			new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
				'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
				'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }))));

		component.onSave();
		expect(alertService.triggerAlert).toHaveBeenCalled();
		expect(component.workbasket.workbasketId).toBe('someNewId');
	});

	it('should post a new workbasket, new distribution targets and new access ' +
		'items when no workbasketId is defined and action is copy', () => {
			const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
				'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
				'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
				'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }));
			component.workbasket = workbasket
			component.action = ACTION.COPY;

			spyOn(workbasketService, 'createWorkbasket').and.returnValue(Observable.of(
				new Workbasket('someNewId', 'created', 'keyModified', 'domain', ICONTYPES.TOPIC, 'modified', 'name', 'description',
					'owner', 'custom1', 'custom2', 'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
					'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }, { 'href': 'someUrl' }, { 'href': 'someUrl' }))));

			spyOn(savingWorkbasketService, 'triggerDistributionTargetSaving');
			spyOn(savingWorkbasketService, 'triggerAccessItemsSaving');

			component.onSave();
			expect(alertService.triggerAlert).toHaveBeenCalled();
			expect(component.workbasket.workbasketId).toBe('someNewId');
			expect(savingWorkbasketService.triggerDistributionTargetSaving).toHaveBeenCalled();
			expect(savingWorkbasketService.triggerAccessItemsSaving).toHaveBeenCalled();
		});

	it('should trigger requestInProgress service true before  and requestInProgress false after remove a workbasket', () => {
		const workbasket = new Workbasket(undefined, 'created', 'keyModified', 'domain', ICONTYPES.TOPIC,
			'modified', 'name', 'description', 'owner', 'custom1', 'custom2',
			'custom3', 'custom4', 'orgLevel1', 'orgLevel2',
			'orgLevel3', 'orgLevel4', new Links({ 'href': 'someUrl' }, undefined, undefined, undefined, { 'href': 'someUrl' }));
		component.workbasket = workbasket;
		spyOn(workbasketService, 'removeDistributionTarget').and.returnValue(Observable.of(''));
		const requestInProgressServiceSpy = spyOn(requestInProgressService, 'setRequestInProgress');


		component.removeDistributionTargets();
		expect(requestInProgressServiceSpy).toHaveBeenCalledWith(true);
		workbasketService.removeDistributionTarget().subscribe(() => {

		}, error => { }, complete => {
			expect(requestInProgressServiceSpy).toHaveBeenCalledWith(false);
		});
	})


});
