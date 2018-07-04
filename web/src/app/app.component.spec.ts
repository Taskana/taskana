import { async, inject, TestBed } from '@angular/core/testing';
import { Router, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'app/shared/shared.module';

import { NavBarComponent } from './components/nav-bar/nav-bar.component';

describe('AppComponent', () => {

	let app, fixture, debugElement;

	const routes: Routes = [
		{ path: 'classifications', component: AppComponent }
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				AppComponent, NavBarComponent
			],
			imports: [
				AngularSvgIconModule,
				RouterTestingModule.withRoutes(routes),
				HttpClientModule,
				SharedModule
			]
		}).compileComponents();

		fixture = TestBed.createComponent(AppComponent);
		app = fixture.debugElement.componentInstance;
		debugElement = fixture.debugElement.nativeElement;

	}));

	afterEach(async(() => {
		document.body.removeChild(debugElement);
	}));

	it('should create the app', (() => {
		expect(app).toBeTruthy();
	}));


	it('should render title in a <a> tag', (() => {
		fixture.detectChanges();
		expect(debugElement.querySelector('ul p a').textContent).toContain('Taskana administration');
	}));

	it('should call Router.navigateByUrl("classifications") and workbasketRoute should be false', (inject([Router], (router: Router) => {

		expect(app.workbasketsRoute).toBe(true);
		fixture.detectChanges();
		router.navigateByUrl(`/classifications`);
		expect(app.workbasketsRoute).toBe(false);

	})));
})
