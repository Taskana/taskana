import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoAccessComponent } from './no-access.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { configureTests } from 'app/app.test.configuration';

describe('NoAccessComponent', () => {
	let component: NoAccessComponent;
	let fixture: ComponentFixture<NoAccessComponent>;
	let debugElement;


	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				imports: [RouterTestingModule, AngularSvgIconModule, HttpClientModule],
				declarations: [NoAccessComponent]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(NoAccessComponent);
			component = fixture.componentInstance;
			debugElement = fixture.debugElement.nativeElement;
			done();
		});

	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
