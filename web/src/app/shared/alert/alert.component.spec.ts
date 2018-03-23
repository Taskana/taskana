import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AlertModel, AlertType } from 'app/models/alert';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertComponent } from './alert.component';

describe('AlertComponent', () => {
	let component: AlertComponent;
	let fixture: ComponentFixture<AlertComponent>;
	let debugElement,
		alertService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [BrowserAnimationsModule],
			declarations: [AlertComponent],
			providers: [AlertService]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		alertService = TestBed.get(AlertService);
		fixture = TestBed.createComponent(AlertComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement.nativeElement;
		fixture.detectChanges();
	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	})

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should show alert message', () => {
		alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'some custom text', ));
		fixture.detectChanges();
		expect(debugElement.querySelector('.alert.alert-success')).toBeDefined();
		expect(debugElement.querySelector('.alert.alert-success').innerText).toBe('some custom text');
	});

	it('should have differents alert types', () => {
		alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'some custom text', ));
		fixture.detectChanges();
		expect(debugElement.querySelector('.alert.alert-danger')).toBeDefined();

		alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'some custom text', ));
		fixture.detectChanges();
		expect(debugElement.querySelector('.alert.alert-warning')).toBeDefined();
	});

	it('should define a closing timeout if alert has autoclosing property', (done) => {
		alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, 'some custom text', true, 5));
		fixture.detectChanges();
		expect(component.alert).toBeDefined();
		setTimeout(() => {
			fixture.detectChanges();
			expect(component.alert).toBeUndefined();
			done();
		}, 6)
	});

	it('should have defined a closing button if alert has no autoclosing property', () => {
		alertService.triggerAlert(new AlertModel(AlertType.DANGER, 'some custom text', false));
		fixture.detectChanges();
		expect(debugElement.querySelector('.alert.alert-danger > button')).toBeDefined();
	});

});
