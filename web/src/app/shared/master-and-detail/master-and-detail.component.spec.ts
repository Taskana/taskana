import { Component } from '@angular/core';
import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, Routes } from '@angular/router';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { MasterAndDetailService } from '../../services/masterAndDetail/master-and-detail.service'

import { MasterAndDetailComponent } from './master-and-detail.component';


@Component({
	selector: 'taskana-dummy-master',
	template: 'dummymaster'
})
export class DummyMasterComponent {

}

@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
export class DummyDetailComponent {

}

describe('MasterAndDetailComponent ', () => {

	let component, fixture, debugElement, router;

	const routes: Routes = [
		{
			path: 'workbaskets',
			component: MasterAndDetailComponent,
			children: [
				{
					path: '',
					component: DummyMasterComponent,
					outlet: 'master'
				},
				{
					path: ':id',
					component: DummyDetailComponent,
					outlet: 'detail'
				}
			]
		}
	];

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				MasterAndDetailComponent,
				DummyMasterComponent,
				DummyDetailComponent],
			imports: [
				RouterTestingModule.withRoutes(routes),
				AngularSvgIconModule,
				HttpClientModule
			],
			providers: [MasterAndDetailService]
		})
			.compileComponents();

		fixture = TestBed.createComponent(MasterAndDetailComponent);
		component = fixture.debugElement.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
		router = TestBed.get(Router);
		router.initialNavigation();

	}));

	afterEach(async(() => {
		document.body.removeChild(debugElement);
	}));

	it('should be created', () => {
		expect(component).toBeTruthy();
	});

	it('should call Router.navigateByUrl("/wokbaskets") and showDetail property should be false', async(() => {

		expect(component.showDetail).toBe(false);
		fixture.detectChanges();
		router.navigateByUrl('/workbaskets');
		expect(component.showDetail).toBe(false);

	}));

	it('should call Router.navigateByUrl("/wokbaskets/(detail:Id)") and showDetail property should be true', async(() => {

		expect(component.showDetail).toBe(false);
		fixture.detectChanges();
		router.navigateByUrl('/workbaskets/(detail:2)');
		expect(component.showDetail).toBe(true);

	}));

	it('should navigate to parent state when backIsClicked', async(() => {

		const spy = spyOn(router, 'navigateByUrl');
		router.navigateByUrl('/workbaskets/(detail:2)');
		fixture.detectChanges();
		expect(spy.calls.first().args[0]).toBe('/workbaskets/(detail:2)');
		component.backClicked();
		expect(spy.calls.mostRecent().args.length).toBe(2);

	}));

});
