import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { WorkbasketService } from '../../../services/workbasket.service';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';
import { Workbasket } from 'app/model/workbasket';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { AlertService } from '../../../services/alert.service';
import { GeneralMessageModalComponent } from '../../../shared/general-message-modal/general-message-modal.component';
import { Links } from '../../../model/links';
import { Observable } from 'rxjs/Observable';
import { AuthorizationsComponent } from './authorizations.component';
import { WorkbasketAuthorization } from '../../../model/workbasket-authorization';

describe('AuthorizationsComponent', () => {
	let component: AuthorizationsComponent;
	let fixture: ComponentFixture<AuthorizationsComponent>;
	let workbasketService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [SpinnerComponent, AuthorizationsComponent, GeneralMessageModalComponent],
			imports: [FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
			providers: [WorkbasketService, AlertService]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuthorizationsComponent);
		component = fixture.componentInstance;
		component.workbasket = new Workbasket('1')
		workbasketService = TestBed.get(WorkbasketService);
		spyOn(workbasketService, 'getWorkBasketAuthorizations').and.returnValue(Observable.of(new Array<WorkbasketAuthorization>(new WorkbasketAuthorization())));

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
