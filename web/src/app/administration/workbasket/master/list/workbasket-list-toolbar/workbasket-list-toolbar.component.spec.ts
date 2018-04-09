import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { Router, Routes } from '@angular/router';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';

import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { Links } from 'app/models/links';
import { FilterModel } from 'app/models/filter';
import { SortingModel } from 'app/models/sorting';

import { SortComponent } from 'app/shared/sort/sort.component';
import { FilterComponent } from 'app/shared/filter/filter.component';
import { IconTypeComponent } from 'app/shared/type-icon/icon-type.component';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';
import { ImportExportComponent } from 'app/shared/import-export/import-export.component';

import { MapValuesPipe } from 'app/pipes/mapValues/map-values.pipe';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationDefinitionService } from 'app/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/services/workbasket-definition/workbasket-definition.service';
import { DomainService } from 'app/services/domains/domain.service';

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
				FilterComponent, IconTypeComponent, DummyDetailComponent, MapValuesPipe, ImportExportComponent],
			providers: [ErrorModalService, WorkbasketService, RequestInProgressService, AlertService,
				ClassificationDefinitionService, WorkbasketDefinitionService, DomainService]
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
