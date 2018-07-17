import { Component } from '@angular/core';
import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';

import { Workbasket } from 'app/models/workbasket';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { ICONTYPES } from 'app/models/type';
import { Links } from 'app/models/links';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { LinksWorkbasketSummary } from 'app/models/links-workbasket-summary';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { AlertService } from 'app/services/alert/alert.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { configureTests } from 'app/app.test.configuration';

import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { WorkbasketInformationComponent } from './information/workbasket-information.component';
import { AccessItemsComponent } from './access-items/access-items.component';
import { DistributionTargetsComponent } from './distribution-targets/distribution-targets.component';
import { DualListComponent } from './distribution-targets//dual-list/dual-list.component';

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
export class DummyDetailComponent {
}

describe('WorkbasketDetailsComponent', () => {
	let component: WorkbasketDetailsComponent;
	let fixture: ComponentFixture<WorkbasketDetailsComponent>;
	let debugElement;
	let masterAndDetailService;
	let workbasketService;
	let router;
	const workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
		new Links({ 'href': 'someurl' }, { 'href': 'someurl' }, { 'href': 'someurl' }));

	const routes: Routes = [
		{ path: '*', component: DummyDetailComponent }
	];

	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				imports: [RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, HttpClientModule, ReactiveFormsModule],
				declarations: [WorkbasketDetailsComponent, WorkbasketInformationComponent,
					AccessItemsComponent,
					DistributionTargetsComponent, DualListComponent, DummyDetailComponent],
				providers: [WorkbasketService, MasterAndDetailService, ErrorModalService, RequestInProgressService,
					AlertService, SavingWorkbasketService,
					CustomFieldsService]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(WorkbasketDetailsComponent);
			component = fixture.componentInstance;
			debugElement = fixture.debugElement.nativeElement;
			router = TestBed.get(Router)
			fixture.detectChanges();
			masterAndDetailService = TestBed.get(MasterAndDetailService);
			workbasketService = TestBed.get(WorkbasketService);
			spyOn(masterAndDetailService, 'getShowDetail').and.callFake(() => { return of(true) })
			spyOn(workbasketService, 'getSelectedWorkBasket').and.callFake(() => { return of('id1') })
			spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => {
				return of(new WorkbasketSummaryResource(
					{
						'workbaskets': new Array<WorkbasketSummary>(
							new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '',
								new Links({ 'href': 'someurl' })))
					}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
			})

			spyOn(workbasketService, 'getWorkBasket').and.callFake(() => { return of(workbasket) })
			spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => {
				return of(new WorkbasketAccessItemsResource(
					{ 'accessItems': new Array<WorkbasketAccessItems>() }, new Links({ 'href': 'url' })))
			})
			spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
				return of(new WorkbasketSummaryResource(
					{ 'workbaskets': new Array<WorkbasketSummary>() }, new LinksWorkbasketSummary({ 'href': 'url' })))
			})
			done();
		});
	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});

	it('should be created', () => {
		expect(component).toBeTruthy();
	});

});
