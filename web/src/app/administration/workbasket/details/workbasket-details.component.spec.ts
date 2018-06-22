import { Component, Input, forwardRef } from '@angular/core';
import { ComponentFixture, TestBed, } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule, NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { Observable } from 'rxjs/Observable';

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

import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { WorkbasketInformationComponent } from './information/workbasket-information.component';
import { AccessItemsComponent } from './access-items/access-items.component';
import { DistributionTargetsComponent } from './distribution-targets/distribution-targets.component';
import { DualListComponent } from './distribution-targets//dual-list/dual-list.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { AlertComponent } from 'app/shared/alert/alert.component';
import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { MapValuesPipe } from 'app/shared/pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from 'app/shared/pipes/removeNoneType/remove-none-type.pipe';
import { SelectWorkBasketPipe } from 'app/shared/pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { configureTests } from 'app/app.test.configuration';
@Component({
	selector: 'taskana-filter',
	template: ''
})
export class FilterComponent {

	@Input()
	target: string;
}

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
				imports: [RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule, ReactiveFormsModule],
				declarations: [WorkbasketDetailsComponent, WorkbasketInformationComponent, SpinnerComponent,
					IconTypeComponent, MapValuesPipe, RemoveNoneTypePipe, AlertComponent, GeneralMessageModalComponent, AccessItemsComponent,
					DistributionTargetsComponent, FilterComponent, DualListComponent, DummyDetailComponent,
					TaskanaTypeAheadComponent, SelectWorkBasketPipe],
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
			spyOn(masterAndDetailService, 'getShowDetail').and.callFake(() => { return Observable.of(true) })
			spyOn(workbasketService, 'getSelectedWorkBasket').and.callFake(() => { return Observable.of('id1') })
			spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => {
				return Observable.of(new WorkbasketSummaryResource(
					{
						'workbaskets': new Array<WorkbasketSummary>(
							new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '',
								new Links({ 'href': 'someurl' })))
					}, new LinksWorkbasketSummary({ 'href': 'someurl' })))
			})

			spyOn(workbasketService, 'getWorkBasket').and.callFake(() => { return Observable.of(workbasket) })
			spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => {
				return Observable.of(new WorkbasketAccessItemsResource(
					{ 'accessItems': new Array<WorkbasketAccessItems>() }, new Links({ 'href': 'url' })))
			})
			spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
				return Observable.of(new WorkbasketSummaryResource(
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
