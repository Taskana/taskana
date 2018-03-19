import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoAccessComponent } from './no-access.component';
import { Router, Routes, ActivatedRoute, NavigationStart, RouterEvent } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';

describe('NoAccessComponent', () => {
	let component: NoAccessComponent;
	let fixture: ComponentFixture<NoAccessComponent>;
	let debugElement;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [RouterTestingModule, AngularSvgIconModule, HttpModule, HttpClientModule],
			declarations: [NoAccessComponent]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(NoAccessComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should have a back button with the following classes "btn btn-default back-button' +
		'pull-left visible-xs visible-sm hidden blue"', () => {
			expect(debugElement.querySelector('button').attributes.class.value)
				.toBe('btn btn-default back-button pull-left visible-xs visible-sm hidden blue');
		});

	it('should have a div with title and svg', () => {
		expect(debugElement.querySelector('div.center-block.no-access > h3').textContent).toBeDefined();
		expect(debugElement.querySelector('div.center-block.no-access > svg-icon').attributes.src.value).toBe('./assets/icons/noaccess.svg');
	});
});
