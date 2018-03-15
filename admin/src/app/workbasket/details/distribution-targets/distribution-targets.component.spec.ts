import { Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';

import { DistributionTargetsComponent, Side } from './distribution-targets.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { GeneralMessageModalComponent } from '../../../shared/general-message-modal/general-message-modal.component';
import { IconTypeComponent } from '../../../shared/type-icon/icon-type.component';
import { SelectWorkBasketPipe } from '../../../pipes/seleted-workbasket.pipe';
import { WorkbasketSummaryResource } from '../../../model/workbasket-summary-resource';
import { WorkbasketSummary } from '../../../model/workbasket-summary';
import { Links } from '../../../model/links';
import { Component } from '@angular/core';
import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService } from '../../../services/alert.service';
import { Observable } from 'rxjs/Observable';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketDistributionTargetsResource } from '../../../model/workbasket-distribution-targets-resource';
import { FilterModel } from '../../../shared/filter/filter.component';
import { DualListComponent } from './dual-list/dual-list.component';

const workbasketSummaryResource: WorkbasketSummaryResource = new WorkbasketSummaryResource({
	'workbaskets': new Array<WorkbasketSummary>(
		new WorkbasketSummary("1", "key1", "NAME1", "description 1", "owner 1", "", "", "PERSONAL", "", "", "", ""),
		new WorkbasketSummary("2", "key2", "NAME2", "description 2", "owner 2", "", "", "GROUP", "", "", "", ""))
}, new Links({ 'href': 'url' }));

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
	let workbasket = new Workbasket('1', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }, { 'href': 'someurl' }, { 'href': 'someurl' }));

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [AngularSvgIconModule, HttpClientModule, HttpModule, JsonpModule],
			declarations: [DistributionTargetsComponent, SpinnerComponent, GeneralMessageModalComponent, FilterComponent, SelectWorkBasketPipe, IconTypeComponent, DualListComponent],
			providers: [WorkbasketService, AlertService]
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
				}, new Links({ 'href': 'someurl' })))
		})
		spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
			return Observable.of(new WorkbasketDistributionTargetsResource(
				{ 'distributionTargets': new Array<WorkbasketSummary>(new WorkbasketSummary('id2', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }))) }, new Links({ 'href': 'someurl' })))
		})

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
				if (leftElement.workbasketId === rightElement.workbasketId) repeteadElemens = true;
			})
		})
		expect(repeteadElemens).toBeFalsy();
	});
	it('should filter left list and keep selected elements as selected', () => {
		component.performFilter({filterBy:new FilterModel(), side: Side.LEFT});
		component.distributionTargetsLeft = new Array<WorkbasketSummary>(
			new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }))
		)
		expect(component.distributionTargetsLeft.length).toBe(1);
		expect(component.distributionTargetsLeft[0].workbasketId).toBe('id1');
		expect(component.distributionTargetsRight.length).toBe(1);
		expect(component.distributionTargetsRight[0].workbasketId).toBe('id2');
	});
	it('should reset distribution target and distribution target selected on reset', () => {
		component.distributionTargetsLeft.push(new WorkbasketSummary('id4', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })));
		component.distributionTargetsRight.push(new WorkbasketSummary('id5', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' })));

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
				}, new Links({ 'href': 'someurl' })))
		})
		component.onSave();
		fixture.detectChanges();
		expect(component.distributionTargetsSelected.length).toBe(2);
		expect(component.distributionTargetsSelectedClone.length).toBe(2);
		expect(component.distributionTargetsLeft.length).toBe(1);
		
	});
});
