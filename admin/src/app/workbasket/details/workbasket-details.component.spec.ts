import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed, } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { NoAccessComponent } from '../noAccess/no-access.component';
import { WorkbasketInformationComponent } from './information/workbasket-information.component';
import { AccessItemsComponent } from './access-items/access-items.component';
import { DistributionTargetsComponent } from './distribution-targets/distribution-targets.component';
import { DualListComponent } from './distribution-targets//dual-list/dual-list.component';
import { Workbasket } from 'app/model/workbasket';
import { Observable } from 'rxjs/Observable';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { IconTypeComponent } from '../../shared/type-icon/icon-type.component';
import { MapValuesPipe } from '../../pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../pipes/remove-none-type';
import { SelectWorkBasketPipe } from '../../pipes/seleted-workbasket.pipe';
import { AlertComponent } from '../../shared/alert/alert.component';
import { GeneralMessageModalComponent } from '../../shared/general-message-modal/general-message-modal.component';
import { Links } from 'app/model/links';
import { WorkbasketAccessItems } from '../../model/workbasket-access-items';

import { WorkbasketService } from '../../services/workbasket.service';
import { MasterAndDetailService } from '../../services/master-and-detail.service';
import { PermissionService } from '../../services/permission.service';
import { AlertService } from '../../services/alert.service';
import { ErrorModalService } from '../../services/error-modal.service';
import { SavingWorkbasketService } from '../../services/saving-workbaskets/saving-workbaskets.service';

import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { WorkbasketSummary } from '../../model/workbasket-summary';
import { WorkbasketSummaryResource } from '../../model/workbasket-summary-resource';
import { WorkbasketAccessItemsResource } from '../../model/workbasket-access-items-resource';
import { ICONTYPES } from '../../model/type';
import { Router, Routes } from '@angular/router';
import { ACTION } from '../../model/action';



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
		{ path: ':id', component: DummyDetailComponent, outlet: 'detail' }
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
			declarations: [WorkbasketDetailsComponent, NoAccessComponent, WorkbasketInformationComponent, SpinnerComponent,
				IconTypeComponent, MapValuesPipe, RemoveNoneTypePipe, AlertComponent, GeneralMessageModalComponent, AccessItemsComponent,
				DistributionTargetsComponent, FilterComponent, DualListComponent, DummyDetailComponent, SelectWorkBasketPipe],
			providers: [WorkbasketService, MasterAndDetailService, PermissionService,
				AlertService, ErrorModalService, SavingWorkbasketService]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(WorkbasketDetailsComponent);
		component = fixture.componentInstance;
		component.hasPermission = false;
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
				}, new Links({ 'href': 'someurl' })))
		})

		spyOn(workbasketService, 'getWorkBasket').and.callFake(() => { return Observable.of(workbasket) })
		spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => {
			return Observable.of(new WorkbasketAccessItemsResource(
				{ 'accessItems': new Array<WorkbasketAccessItems>() }, new Links({ 'href': 'url' })))
		})
		spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
			return Observable.of(new WorkbasketSummaryResource(
				{ 'workbaskets': new Array<WorkbasketSummary>() }, new Links({ 'href': 'url' })))
		})

	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});

	it('should be created', () => {
		expect(component).toBeTruthy();
	});

	it('should has created taskana-no-access if workbasket is not defined and hasPermission is false', () => {
		expect(component.workbasket).toBeUndefined();
		component.hasPermission = false;
		fixture.detectChanges();
		expect(debugElement.querySelector('taskana-no-access')).toBeTruthy();
	});

	it('should has created taskana-workbasket-details if workbasket is defined and taskana-no-access should dissapear', () => {
		expect(component.workbasket).toBeUndefined();
		component.hasPermission = false;
		fixture.detectChanges();
		expect(debugElement.querySelector('taskana-no-access')).toBeTruthy();

		component.workbasket = workbasket;
		fixture.detectChanges();

		expect(debugElement.querySelector('taskana-no-access')).toBeFalsy();
		expect(debugElement.querySelector('taskana-workbasket-information')).toBeTruthy();

	});

});
