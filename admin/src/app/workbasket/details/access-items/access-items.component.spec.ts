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
import { AccessItemsComponent } from './access-items.component';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';

describe('AccessItemsComponent', () => {
	let component: AccessItemsComponent;
	let fixture: ComponentFixture<AccessItemsComponent>;
	let workbasketService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [SpinnerComponent, AccessItemsComponent, GeneralMessageModalComponent],
			imports: [FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
			providers: [WorkbasketService, AlertService]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AccessItemsComponent);
		component = fixture.componentInstance;
		component.workbasket = new Workbasket('1')
		workbasketService = TestBed.get(WorkbasketService);
		spyOn(workbasketService, 'getWorkBasketAccessItems').and.returnValue(Observable.of(new Array<WorkbasketAccessItems>(new WorkbasketAccessItems())));

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
