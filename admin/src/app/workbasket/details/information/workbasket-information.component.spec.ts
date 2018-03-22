import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { WorkbasketService } from '../../../services/workbasket.service';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';
import { Workbasket } from 'app/model/workbasket';
import { IconTypeComponent } from '../../../shared/type-icon/icon-type.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { GeneralMessageModalComponent } from '../../../shared/general-message-modal/general-message-modal.component';
import { MapValuesPipe } from '../../../pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../../pipes/remove-none-type';
import { AlertService } from '../../../services/alert.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Links } from '../../../model/links';
import { Observable } from 'rxjs/Observable';
import { ICONTYPES } from '../../../model/type';
import { ErrorModalService } from '../../../services/error-modal.service';
import { SavingWorkbasketService, SavingInformation } from '../../../services/saving-workbaskets/saving-workbaskets.service';
import { ACTION } from '../../../model/action';

describe('InformationComponent', () => {
	let component: WorkbasketInformationComponent;
	let fixture: ComponentFixture<WorkbasketInformationComponent>;
	let debugElement, workbasketService, alertService, savingWorkbasketService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [WorkbasketInformationComponent, IconTypeComponent, MapValuesPipe,
				RemoveNoneTypePipe, SpinnerComponent, GeneralMessageModalComponent],
			imports: [FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule, RouterTestingModule],
			providers: [WorkbasketService, AlertService, SavingWorkbasketService, ErrorModalService]

		})
			.compileComponents();
		fixture = TestBed.createComponent(WorkbasketInformationComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
		workbasketService = TestBed.get(WorkbasketService);
		alertService = TestBed.get(AlertService);
		savingWorkbasketService = TestBed.get(SavingWorkbasketService);
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
		component.ngOnInit();
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
		expect(component.modalSpinner).toBeTruthy();
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

});
