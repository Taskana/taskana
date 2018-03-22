import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { Router, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ErrorModalService } from '../../../services/error-modal.service';
import { WorkbasketService } from '../../../services/workbasket.service';
import { RequestInProgressService } from '../../../services/request-in-progress.service';
import { AlertService } from '../../../services/alert.service';
import { SortComponent, SortingModel } from '../../../shared/sort/sort.component';
import { FilterComponent, FilterModel } from '../../../shared/filter/filter.component';
import { IconTypeComponent } from '../../../shared/type-icon/icon-type.component';
import { MapValuesPipe } from '../../../pipes/map-values.pipe';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';
import { Component } from '@angular/core';
import { WorkbasketSummary } from '../../../model/workbasket-summary';
import { Links } from '../../../model/links';
import { Observable } from 'rxjs/Observable';

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
export class DummyDetailComponent {

}

describe('WorkbasketListToolbarComponent', () => {
	let component: WorkbasketListToolbarComponent;
	let fixture: ComponentFixture<WorkbasketListToolbarComponent>;
	let debugElement, workbasketService, requestInProgressService, router;

	const routes: Routes = [
		{ path: ':id', component: DummyDetailComponent, outlet: 'detail' }
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [FormsModule, ReactiveFormsModule, AngularSvgIconModule, HttpModule,
				HttpClientModule, RouterTestingModule.withRoutes(routes)],
			declarations: [WorkbasketListToolbarComponent, SortComponent,
				FilterComponent, IconTypeComponent, DummyDetailComponent, MapValuesPipe],
			providers: [ErrorModalService, WorkbasketService, RequestInProgressService, AlertService]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(WorkbasketListToolbarComponent);
		workbasketService = TestBed.get(WorkbasketService);
		requestInProgressService = TestBed.get(RequestInProgressService);
		router = TestBed.get(Router);
		spyOn(workbasketService, 'deleteWorkbasket').and.returnValue(Observable.of(''));
		spyOn(workbasketService, 'triggerWorkBasketSaved');
		spyOn(requestInProgressService, 'setRequestInProgress');

		debugElement = fixture.debugElement.nativeElement;
		component = fixture.componentInstance;
		component.workbaskets = new Array<WorkbasketSummary>(
			new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1',
				undefined, undefined, undefined, undefined, undefined, undefined, undefined, new Links({ 'href': 'selfLink' })));
		component.workbasketIdSelected = '1';

		fixture.detectChanges();
	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should navigate to new-workbasket when click on add new workbasket', () => {
		const spy = spyOn(router, 'navigate');
		component.addWorkbasket();
		expect(spy.calls.first().args[0][0].outlets.detail[0]).toBe('new-workbasket');
	});


	it('should navigate to copy-workbasket when click on add copy workbasket', () => {
		const spy = spyOn(router, 'navigate');
		component.copyWorkbasket();
		expect(spy.calls.first().args[0][0].outlets.detail[0]).toBe('copy-workbasket');
	});


	it('should call to workbasket service to remove workbasket after click on remove workbasket', () => {
		const spy = spyOn(router, 'navigate');
		component.removeWorkbasket();
		expect(requestInProgressService.setRequestInProgress).toHaveBeenCalledWith(true);
		expect(workbasketService.deleteWorkbasket).toHaveBeenCalledWith('selfLink');
		expect(requestInProgressService.setRequestInProgress).toHaveBeenCalledWith(false);
		expect(workbasketService.triggerWorkBasketSaved).toHaveBeenCalled();
		expect(spy.calls.first().args[0][0]).toBe('/workbaskets');
	});

	it('should emit performSorting when sorting is triggered', () => {
		let sort: SortingModel;
		const compareSort = new SortingModel();

		component.performSorting.subscribe((value) => { sort = value })
		component.sorting(compareSort);
		expect(sort).toBe(compareSort);

	});

	it('should emit performFilter when filter is triggered', () => {
		let filter: FilterModel;
		const compareFilter = new FilterModel();

		component.performFilter.subscribe((value) => { filter = value })
		component.filtering(compareFilter);
		expect(filter).toBe(compareFilter);
	});

});
