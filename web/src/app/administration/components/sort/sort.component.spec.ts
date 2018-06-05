import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MapValuesPipe } from 'app/shared/pipes/mapValues/map-values.pipe';

import { SortComponent } from './sort.component';
import { Direction } from 'app/models/sorting';
import { configureTests } from 'app/app.test.configuration';

describe('SortComponent', () => {
	let component: SortComponent;
	let fixture: ComponentFixture<SortComponent>;
	let debugElement;

	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				declarations: [SortComponent, MapValuesPipe]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(SortComponent);
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

	it('should change order when click on order ', () => {
		expect(component.sort.sortDirection).toBe(Direction.ASC);
		debugElement.querySelector('#sort-by-direction-desc').click();
		expect(component.sort.sortDirection).toBe(Direction.DESC);
	});

	it('should change sort by when click on sort by ', () => {
		expect(component.sort.sortBy).toBe('key');
		debugElement.querySelector('#sort-by-name').click();
		expect(component.sort.sortBy).toBe('name');
	});
});
