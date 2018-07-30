import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Router, Routes } from '@angular/router';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedModule } from 'app/shared/shared.module';
import { AppModule } from 'app/app.module';

import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { Links } from 'app/models/links';
import { FilterModel } from 'app/models/filter';
import { SortingModel } from 'app/models/sorting';

import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';
import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition/workbasket-definition.service';
import { configureTests } from 'app/app.test.configuration';

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
export class DummyDetailComponent {

}

describe('WorkbasketListToolbarComponent', () => {
	let component: WorkbasketListToolbarComponent;
	let fixture: ComponentFixture<WorkbasketListToolbarComponent>;
	let debugElement, workbasketService, router;

	const routes: Routes = [
		{ path: ':id', component: DummyDetailComponent, outlet: 'detail' }
	];

	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				imports: [FormsModule, ReactiveFormsModule, AngularSvgIconModule,
					HttpClientModule, RouterTestingModule.withRoutes(routes), SharedModule, AppModule],
				declarations: [WorkbasketListToolbarComponent, DummyDetailComponent, ImportExportComponent],
				providers: [
					WorkbasketService,
					ClassificationDefinitionService,
					WorkbasketDefinitionService,
				]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(WorkbasketListToolbarComponent);
			workbasketService = TestBed.get(WorkbasketService);
			router = TestBed.get(Router);
			spyOn(workbasketService, 'deleteWorkbasket').and.returnValue(of(''));
			spyOn(workbasketService, 'triggerWorkBasketSaved');

			debugElement = fixture.debugElement.nativeElement;
			component = fixture.componentInstance;
			component.workbaskets = new Array<WorkbasketSummary>(
				new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1',
					undefined, undefined, undefined, undefined, undefined, undefined, undefined, new Links({ 'href': 'selfLink' })));

			fixture.detectChanges();
			done();
		});
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
