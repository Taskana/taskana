import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { WorkbasketService } from '../../../services/workbasket.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';
import { Workbasket } from 'app/model/workbasket';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';
import { GeneralMessageModalComponent } from '../../../shared/general-message-modal/general-message-modal.component';
import { Links } from '../../../model/links';
import { Observable } from 'rxjs/Observable';
import { AccessItemsComponent } from './access-items.component';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';
import { WorkbasketAccessItemsResource } from '../../../model/workbasket-access-items-resource';
import { ICONTYPES } from '../../../model/type';
import { ErrorModalService } from '../../../services/error-modal.service';
import { SavingWorkbasketService, SavingInformation } from '../../../services/saving-workbaskets/saving-workbaskets.service';

describe('AccessItemsComponent', () => {
	let component: AccessItemsComponent;
	let fixture: ComponentFixture<AccessItemsComponent>;
	let workbasketService, debugElement, alertService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [SpinnerComponent, AccessItemsComponent, GeneralMessageModalComponent],
			imports: [FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule, ReactiveFormsModule],
			providers: [WorkbasketService, AlertService, ErrorModalService, SavingWorkbasketService]

		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AccessItemsComponent);
		component = fixture.componentInstance;
		component.workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
			new Links(undefined, undefined, { 'href': 'someurl' }));
		workbasketService = TestBed.get(WorkbasketService);
		alertService = TestBed.get(AlertService);
		spyOn(workbasketService, 'getWorkBasketAccessItems').and.returnValue(Observable.of(new WorkbasketAccessItemsResource(
			{
				'accessItems': new Array<WorkbasketAccessItems>(
					new WorkbasketAccessItems('id1', '1', 'accessID1', false, false, false, false, false, false, false, false,
						false, false, false, false, false, false, false, false, false),
					new WorkbasketAccessItems('id2', '1', 'accessID2'))
			}, new Links({ 'href': 'someurl' })
		)));
		spyOn(workbasketService, 'updateWorkBasketAccessItem').and.returnValue(Observable.of(true)),
			spyOn(alertService, 'triggerAlert').and.returnValue(Observable.of(true)),
			debugElement = fixture.debugElement.nativeElement;

		fixture.detectChanges();
	});

	afterEach(() => {
		document.body.removeChild(debugElement);
	});


	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should show two access items if server returns two entries', () => {
		expect(debugElement.querySelectorAll('#table-access-items > tbody > tr').length).toBe(2);

	});

	it('should show Add new access item button', () => {
		expect(debugElement.querySelector('#button-add-access-item')).toBeTruthy();
	});

	it('should remove an access item if remove button is clicked', () => {
		expect(debugElement.querySelectorAll('#table-access-items > tbody > tr').length).toBe(2);
		debugElement.querySelectorAll('#table-access-items > tbody > tr')[0].querySelector('td > button').click();
		fixture.detectChanges();
		expect(debugElement.querySelectorAll('#table-access-items > tbody > tr').length).toBe(1);
	});

	it('should show alert successfull after saving', () => {
		component.onSave();
		expect(alertService.triggerAlert).toHaveBeenCalledWith(
			new AlertModel(AlertType.SUCCESS, `Workbasket  ${component.workbasket.key} Access items were saved successfully`));
	});

});
