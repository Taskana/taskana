import { Component, Input, Output, EventEmitter } from '@angular/core';
import { async, ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';

import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { FilterModel } from 'app/models/filter';
import { LinksWorkbasketSummary } from 'app/models/links-workbasket-summary';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { OrientationService } from 'app/services/orientation/orientation.service';

import { WorkbasketListComponent } from './workbasket-list.component';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar/workbasket-list-toolbar.component';
import { IconTypeComponent } from 'app/shared/type-icon/icon-type.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { SortComponent } from 'app/shared/sort/sort.component';
import { ImportExportComponent } from 'app/shared/import-export/import-export.component';

import { RemoveNoneTypePipe } from 'app/pipes/removeNoneType/remove-none-type.pipe';
import { MapValuesPipe } from 'app/pipes/mapValues/map-values.pipe';
import { WorkbasketDefinitionService } from 'app/services/workbasket-definition/workbasket-definition.service';
import { ClassificationDefinitionService } from 'app/services/classification-definition/classification-definition.service';
import { DomainService } from 'app/services/domains/domain.service';

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
class DummyDetailComponent {
}

@Component({
	selector: 'taskana-pagination',
	template: 'dummydetail'
})
class PaginationComponent {
	@Input()
	workbasketsResource: any;
	@Output()
	workbasketsResourceChange = new EventEmitter<any>();
	@Output() changePage = new EventEmitter<any>();
}

@Component({
	selector: 'taskana-filter',
	template: ''
})
class FilterComponent {

}

const workbasketSummaryResource: WorkbasketSummaryResource = new WorkbasketSummaryResource({
	'workbaskets': new Array<WorkbasketSummary>(
		new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1', '', '', 'PERSONAL', '', '', '', ''),
		new WorkbasketSummary('2', 'key2', 'NAME2', 'description 2', 'owner 2', '', '', 'GROUP', '', '', '', ''))
}, new LinksWorkbasketSummary({ 'href': 'url' }));


describe('WorkbasketListComponent', () => {
	let component: WorkbasketListComponent;
	let fixture: ComponentFixture<WorkbasketListComponent>;
	let debugElement: any = undefined;
	let workbasketService: WorkbasketService;
	let workbasketSummarySpy;

	const routes: Routes = [
		{ path: ':id', component: DummyDetailComponent, outlet: 'detail' },
		{ path: 'workbaskets', component: DummyDetailComponent }
	];


	beforeEach(async(() => {
		TestBed.configureTestingModule({

			declarations: [WorkbasketListComponent, DummyDetailComponent, SpinnerComponent, FilterComponent, WorkbasketListToolbarComponent,
				RemoveNoneTypePipe, IconTypeComponent, SortComponent, PaginationComponent, ImportExportComponent, MapValuesPipe],
			imports: [
				AngularSvgIconModule,
				HttpModule,
				HttpClientModule,
				RouterTestingModule.withRoutes(routes)
			],
			providers: [WorkbasketService, ErrorModalService, RequestInProgressService, AlertService,
				WorkbasketDefinitionService, OrientationService, DomainService, ClassificationDefinitionService]
		})
			.compileComponents();


		fixture = TestBed.createComponent(WorkbasketListComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
		workbasketService = TestBed.get(WorkbasketService);
		workbasketSummarySpy = spyOn(workbasketService, 'getWorkBasketsSummary').and.returnValue(Observable.of(workbasketSummaryResource));
		spyOn(workbasketService, 'getSelectedWorkBasket').and.returnValue(Observable.of('2'));

		fixture.detectChanges();
	}));

	afterEach(() => {
		fixture.detectChanges()
		document.body.removeChild(debugElement);
	})

	it('should be created', () => {
		expect(component).toBeTruthy();
	});

	it('should call workbasketService.getWorkbasketsSummary method on init', () => {
		component.ngOnInit();
		expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalled();
		workbasketService.getWorkBasketsSummary().subscribe(value => {
			expect(value).toBe(workbasketSummaryResource);
		})
	});

	it('should have wb-action-toolbar, wb-search-bar, wb-list-container, wb-pagination,' +
		' collapsedMenufilterWb and taskana-filter created in the html', () => {
			expect(debugElement.querySelector('#wb-action-toolbar')).toBeDefined();
			expect(debugElement.querySelector('#wb-search-bar')).toBeDefined();
			expect(debugElement.querySelector('#wb-pagination')).toBeDefined();
			expect(debugElement.querySelector('#wb-list-container')).toBeDefined();
			expect(debugElement.querySelector('#collapsedMenufilterWb')).toBeDefined();
			expect(debugElement.querySelector('taskana-filter')).toBeDefined();
			expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(3);
		});

	it('should have two workbasketsummary rows created with the second one selected.', fakeAsync(() => {
		tick(0);
		fixture.detectChanges();
		fixture.whenStable().then(() => {
			expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(3);
			expect(debugElement.querySelectorAll('#wb-list-container > li')[1].getAttribute('class')).toBe('list-group-item');
			expect(debugElement.querySelectorAll('#wb-list-container > li')[2].getAttribute('class')).toBe('list-group-item active');
		})

	}));

	it('should have two workbasketsummary rows created with two different icons: user and users', () => {
		expect(debugElement.querySelectorAll('#wb-list-container > li')[1]
			.querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/user.svg');
		expect(debugElement.querySelectorAll('#wb-list-container > li')[2]
			.querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/users.svg');
	});

	it('should have rendered sort by: name, id, description, owner and type', () => {
		expect(debugElement.querySelector('#sort-by-name')).toBeDefined();
		expect(debugElement.querySelector('#sort-by-key')).toBeDefined();
		expect(debugElement.querySelector('#sort-by-description')).toBeDefined();
		expect(debugElement.querySelector('#sort-by-owner')).toBeDefined();
		expect(debugElement.querySelector('#sort-by-type')).toBeDefined();

	});

	it('should have performRequest with forced = true after performFilter is triggered', (() => {
		const type = 'PERSONAL', name = 'someName', description = 'someDescription', owner = 'someOwner', key = 'someKey';
		const filter = new FilterModel(type, name, description, owner, key);
		component.performFilter(filter);

		expect(workbasketSummarySpy.calls.all()[1].args).toEqual([true, 'key', 'asc',
			undefined, 'someName', 'someDescription', undefined, 'someOwner', 'PERSONAL', undefined, 'someKey', undefined]);

	}));

});
