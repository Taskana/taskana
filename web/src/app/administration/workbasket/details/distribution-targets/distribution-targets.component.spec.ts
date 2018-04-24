import { Input, Component, SimpleChange } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';

import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { ICONTYPES } from 'app/models/type';
import { Links } from 'app/models/links';
import { FilterModel } from 'app/models/filter';
import { Workbasket } from 'app/models/workbasket';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';

import { WorkbasketService } from 'app/administration/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

import { DualListComponent } from './dual-list/dual-list.component';
import { DistributionTargetsComponent, Side } from './distribution-targets.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { SelectWorkBasketPipe } from 'app/shared/pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { LinksWorkbasketSummary } from 'app/models/links-workbasket-summary';
import { DomainService } from 'app/services/domain/domain.service';
import { DomainServiceMock } from 'app/services/domain/domain.service.mock';


const workbasketSummaryResource: WorkbasketSummaryResource = new WorkbasketSummaryResource({
	'workbaskets': new Array<WorkbasketSummary>(
		new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1', '', '', 'PERSONAL', '', '', '', ''),
		new WorkbasketSummary('2', 'key2', 'NAME2', 'description 2', 'owner 2', '', '', 'GROUP', '', '', '', ''))
}, new LinksWorkbasketSummary({ 'href': 'url' }));

@Component({
	selector: 'taskana-filter',
	template: ''

})
export class FilterComponent {

	@Input()
	target: string;
}

describe('DistributionTargetsComponent', () => {
	let component: DistributionTargetsComponent;
	let fixture: ComponentFixture<DistributionTargetsComponent>;
	let workbasketService;
	const workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
		new Links({ 'href': 'someurl' }, { 'href': 'someurl' }, { 'href': 'someurl' }));

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [AngularSvgIconModule, HttpClientModule, HttpModule, JsonpModule],
			declarations: [DistributionTargetsComponent, SpinnerComponent, GeneralMessageModalComponent,
				FilterComponent, SelectWorkBasketPipe, IconTypeComponent, DualListComponent],
			providers: [WorkbasketService, AlertService, SavingWorkbasketService, ErrorModalService, RequestInProgressService,
				{
					provide: DomainService,
					useClass: DomainServiceMock
				  }]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(DistributionTargetsComponent);
		component = fixture.componentInstance;
		component.workbasket = workbasket;
		workbasketService = TestBed.get(WorkbasketService);
		spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => {
			return Observable.of(new WorkbasketSummaryResource(
				{
					'workbaskets': new Array<WorkbasketSummary>(
						new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })),
						new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })),
						new WorkbasketSummary('id3', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })))
				}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
		})
		spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
			return Observable.of(new WorkbasketDistributionTargetsResource(
				{
					'distributionTargets': new Array<WorkbasketSummary>(
						new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })))
				}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
		})
		component.ngOnChanges({
			active: new SimpleChange(undefined, 'distributionTargets', true)
		});
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});


	it('should clone distribution target selected on init', () => {
		expect(component.distributionTargetsClone).toBeDefined();
	});
	it('should clone distribution target left and distribution target right lists on init', () => {
		expect(component.distributionTargetsLeft).toBeDefined();
		expect(component.distributionTargetsRight).toBeDefined();
	});

	it('should have two list with differents elements onInit', () => {
		let repeteadElemens = false;
		expect(component.distributionTargetsLeft.length).toBe(2);
		expect(component.distributionTargetsRight.length).toBe(1);
		component.distributionTargetsLeft.forEach(leftElement => {
			component.distributionTargetsRight.forEach(rightElement => {
				if (leftElement.workbasketId === rightElement.workbasketId) { repeteadElemens = true };
			})
		})
		expect(repeteadElemens).toBeFalsy();
	});
	it('should filter left list and keep selected elements as selected', () => {
		component.performFilter({ filterBy: new FilterModel(), side: Side.LEFT });
		component.distributionTargetsLeft = new Array<WorkbasketSummary>(
			new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }))
		)
		expect(component.distributionTargetsLeft.length).toBe(1);
		expect(component.distributionTargetsLeft[0].workbasketId).toBe('id1');
		expect(component.distributionTargetsRight.length).toBe(1);
		expect(component.distributionTargetsRight[0].workbasketId).toBe('id2');
	});
	it('should reset distribution target and distribution target selected on reset', () => {
		component.distributionTargetsLeft.push(
			new WorkbasketSummary('id4', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })));
		component.distributionTargetsRight.push(
			new WorkbasketSummary('id5', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })));

		expect(component.distributionTargetsLeft.length).toBe(3);
		expect(component.distributionTargetsRight.length).toBe(2);

		component.onClear();
		fixture.detectChanges();
		expect(component.distributionTargetsLeft.length).toBe(2);
		expect(component.distributionTargetsRight.length).toBe(1)
	});

	it('should save distribution targets selected and update Clone objects.', () => {
		expect(component.distributionTargetsSelected.length).toBe(1);
		expect(component.distributionTargetsSelectedClone.length).toBe(1);
		spyOn(workbasketService, 'updateWorkBasketsDistributionTargets').and.callFake(() => {
			return Observable.of(new WorkbasketDistributionTargetsResource(
				{
					'distributionTargets': new Array<WorkbasketSummary>(
						new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })),
						new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })))
				}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
		})
		component.onSave();
		fixture.detectChanges();
		expect(component.distributionTargetsSelected.length).toBe(2);
		expect(component.distributionTargetsSelectedClone.length).toBe(2);
		expect(component.distributionTargetsLeft.length).toBe(1);

	});
});
