import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { ICONTYPES } from 'app/models/type';
import { Links } from 'app/models/links';
import { FilterModel } from 'app/models/filter';
import { Workbasket } from 'app/models/workbasket';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

import { DualListComponent } from './dual-list/dual-list.component';
import { DistributionTargetsComponent, Side } from './distribution-targets.component';
import { LinksWorkbasketSummary } from 'app/models/links-workbasket-summary';
import { configureTests } from 'app/app.test.configuration';

describe('DistributionTargetsComponent', () => {
	let component: DistributionTargetsComponent;
	let fixture: ComponentFixture<DistributionTargetsComponent>;
	let workbasketService;
	const workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
		new Links({ 'href': 'someurl' }, { 'href': 'someurl' }, { 'href': 'someurl' }));

	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				imports: [AngularSvgIconModule, HttpClientModule],
				declarations: [DistributionTargetsComponent, DualListComponent],
				providers: [WorkbasketService, AlertService, SavingWorkbasketService, ErrorModalService, RequestInProgressService,
				]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(DistributionTargetsComponent);
			component = fixture.componentInstance;
			component.workbasket = workbasket;
			workbasketService = TestBed.get(WorkbasketService);
			spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => {
				return of(new WorkbasketSummaryResource(
					{
						'workbaskets': new Array<WorkbasketSummary>(
							new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })),
							new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })),
							new WorkbasketSummary('id3', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })))
					}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
			})
			spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
				return of(new WorkbasketDistributionTargetsResource(
					{
						'distributionTargets': new Array<WorkbasketSummary>(
							new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })))
					}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
			})
			component.ngOnChanges({
				active: new SimpleChange(undefined, 'distributionTargets', true)
			});
			fixture.detectChanges();
			done();
		});
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
		component.performFilter({ filterBy: new FilterModel({
      name: 'someName', owner: 'someOwner', description: 'someDescription', key: 'someKey'}), side: Side.LEFT });
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
			return of(new WorkbasketDistributionTargetsResource(
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
