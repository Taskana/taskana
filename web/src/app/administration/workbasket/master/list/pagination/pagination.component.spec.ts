import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SimpleChange } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared/shared.module';

import { PaginationComponent } from './pagination.component';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { Page } from 'app/models/page';
import { configureTests } from 'app/app.test.configuration';

describe('PaginationComponent', () => {
	let component: PaginationComponent;
	let fixture: ComponentFixture<PaginationComponent>;
	let debugElement;


	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				declarations: [
					PaginationComponent
				],
				imports: [FormsModule, SharedModule]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(PaginationComponent);
			component = fixture.componentInstance;
			debugElement = fixture.debugElement.nativeElement;
			fixture.detectChanges();
			done();
		});
	});

	afterEach(() => {
		fixture.detectChanges()
		document.body.removeChild(debugElement);
	})

	it('should create', () => {
		expect(component).toBeTruthy();
		expect(debugElement.querySelectorAll('#wb-pagination > li').length).toBe(2);
	});

	it('should create 3 pages if total pages are 3', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 1));
		fixture.detectChanges();
		expect(debugElement.querySelectorAll('#wb-pagination > li').length).toBe(5);
	});

	it('should emit change if previous page was different than current one', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 1));
		component.previousPageSelected = 2;
		fixture.detectChanges();
		component.changePage.subscribe(value => {
			expect(value).toBe(1)
		})
		component.changeToPage(1);
	});

	it('should not emit change if previous page was the same than current one', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 1));
		component.previousPageSelected = 2;
		fixture.detectChanges();
		component.changePage.subscribe(value => {
			expect(false).toBe(true)
		})
		component.changeToPage(2);
	});

	it('should emit totalPages if page is more than page.totalPages', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 1));
		component.previousPageSelected = 2;
		fixture.detectChanges();
		component.changePage.subscribe(value => {
			expect(value).toBe(3)
		})
		component.changeToPage(100);
	});

	it('should emit 1 if page is less than 1', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 1));
		component.previousPageSelected = 2;
		fixture.detectChanges();
		component.changePage.subscribe(value => {
			expect(value).toBe(1)
		})
		component.changeToPage(0);
	});

	it('should change pageSelected onChanges', () => {
		expect(component.pageSelected).toBe(1);
		component.ngOnChanges({
			workbasketsResource: new SimpleChange(null, new WorkbasketSummaryResource(undefined, undefined, new Page(6, 3, 3, 2)), true)
		});
		fixture.detectChanges();
		expect(component.pageSelected).toBe(2);

	});

	it('should getPagesTextToShow return 7 of 13 with size < totalElements', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(7, 13, 3, 2));
		expect(component.getPagesTextToShow()).toBe('7 of 13 workbaskets');
	});

	it('should getPagesTextToShow return 6 of 6 with size > totalElements', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(7, 6, 3, 2));
		expect(component.getPagesTextToShow()).toBe('6 of 6 workbaskets');
	});

	it('should getPagesTextToShow return  of  with totalElements = 0', () => {
		component.workbasketsResource = new WorkbasketSummaryResource(undefined, undefined, new Page(7, 0, 0, 0));
		expect(component.getPagesTextToShow()).toBe('0 of 0 workbaskets');
	});

});
