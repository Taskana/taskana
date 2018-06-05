import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { FilterModel } from 'app/models/filter';
import { IconTypeComponent } from '../type-icon/icon-type.component';
import { FilterComponent } from './filter.component';
import { MapValuesPipe } from 'app/shared/pipes/mapValues/map-values.pipe';
import { configureTests } from 'app/app.test.configuration';

describe('FilterComponent', () => {
	let component: FilterComponent,
		fixture: ComponentFixture<FilterComponent>,
		debugElement: any;


	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				declarations: [FilterComponent, IconTypeComponent, MapValuesPipe],
				imports: [AngularSvgIconModule, FormsModule, HttpClientModule, HttpModule]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(FilterComponent);
			component = fixture.componentInstance;
			debugElement = fixture.debugElement.nativeElement;
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

	it('should create a component with id target', () => {
		expect(debugElement.querySelector('#some-id')).toBeNull();
		component.target = 'some-id'
		fixture.detectChanges();
		expect(debugElement.querySelector('#some-id')).toBeDefined();
	});

	it('should have filter by: name, description, key, owner and type  defined', () => {
		expect(debugElement.querySelector('#wb-display-name-filter')).toBeDefined();
		expect(debugElement.querySelector('#wb-display-description-filter')).toBeDefined();
		expect(debugElement.querySelector('#wb-display-key-filter')).toBeDefined();
		expect(debugElement.querySelector('#wb-display-owner-filter')).toBeDefined();
		expect(debugElement.querySelector('#wb-display-type-filter')).toBeDefined();
	});

	it('should be able to clear all fields after pressing clear button', () => {
		component.filter = new FilterModel('a', 'a', 'a', 'a', 'a');
		debugElement.querySelector('[title="Clear"]').click();
		expect(component.filter.name).toBe('');
		expect(component.filter.description).toBe('');
		expect(component.filter.owner).toBe('');
		expect(component.filter.type).toBe('');
		expect(component.filter.key).toBe('');
	});

	it('should be able to select a type and return it based on a number', () => {
		expect(component).toBeTruthy();
	});

	it('should be able to emit a filter after clicking on search button', (done) => {
		component.filter = new FilterModel('a', 'name1', 'a', 'a');
		component.performFilter.subscribe(filter => {
			expect(filter.name).toBe('name1');
			done();
		})
		debugElement.querySelector('[title="Search"]').click();
	});

});
