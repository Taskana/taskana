import { async, inject, TestBed } from '@angular/core/testing';
import { Router, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';

import { ErrorModalService } from './services/errorModal/error-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { AlertService } from './services/alert/alert.service';
import { OrientationService } from './services/orientation/orientation.service';
import { SelectedRouteService } from './services/selected-route/selected-route';

import { GeneralMessageModalComponent } from './shared/general-message-modal/general-message-modal.component'
import { SpinnerComponent } from './shared/spinner/spinner.component'
import { AlertComponent } from './shared/alert/alert.component';
import { NavBarComponent } from './shared/nav-bar/nav-bar.component';


describe('AppComponent', () => {

	let app, fixture, debugElement;

	const routes: Routes = [
		{ path: 'classifications', component: AppComponent }
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				AppComponent, GeneralMessageModalComponent, SpinnerComponent, AlertComponent, NavBarComponent
			],
			imports: [
				AngularSvgIconModule,
				RouterTestingModule.withRoutes(routes),
				HttpClientModule
			],
			providers: [ErrorModalService, RequestInProgressService, AlertService, OrientationService, SelectedRouteService]
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
