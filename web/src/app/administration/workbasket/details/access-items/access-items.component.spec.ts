import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { of } from 'rxjs';
import { configureTests } from 'app/app.test.configuration';

import { Workbasket } from 'app/models/workbasket';
import { AlertModel, AlertType } from 'app/models/alert';
import { Links } from 'app/models/links';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { ICONTYPES } from 'app/models/type';

import { AccessItemsComponent } from './access-items.component';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';

describe('AccessItemsComponent', () => {
	let component: AccessItemsComponent;
	let fixture: ComponentFixture<AccessItemsComponent>;
	let workbasketService, debugElement, alertService, accessIdsService, formsValidatorService;


	beforeEach(done => {
		const configure = (testBed: TestBed) => {
			testBed.configureTestingModule({
				declarations: [AccessItemsComponent],
				imports: [FormsModule, AngularSvgIconModule, HttpClientModule, ReactiveFormsModule],
				providers: [WorkbasketService, AlertService, ErrorModalService, SavingWorkbasketService, RequestInProgressService,
					CustomFieldsService, AccessIdsService, FormsValidatorService]
			})
		};
		configureTests(configure).then(testBed => {
			fixture = TestBed.createComponent(AccessItemsComponent);
			component = fixture.componentInstance;
			component.workbasket = new Workbasket('1', '', '', '', ICONTYPES.TOPIC, '', '', '', '', '', '', '', '', '', '', '', '',
				new Links(undefined, undefined, { 'href': 'someurl' }));
			workbasketService = TestBed.get(WorkbasketService);
			alertService = TestBed.get(AlertService);
			spyOn(workbasketService, 'getWorkBasketAccessItems').and.returnValue(of(new WorkbasketAccessItemsResource(
				{
					'accessItems': new Array<WorkbasketAccessItems>(
						new WorkbasketAccessItems('id1', '1', 'accessID1', false, false, false, false, false, false, false, false,
							false, false, false, false, false, false, false, false, false),
						new WorkbasketAccessItems('id2', '1', 'accessID2'))
				}, new Links({ 'href': 'someurl' })
			)));
			spyOn(workbasketService, 'updateWorkBasketAccessItem').and.returnValue(of(true)),
				spyOn(alertService, 'triggerAlert').and.returnValue(of(true)),
        debugElement = fixture.debugElement.nativeElement;
      accessIdsService = TestBed.get(AccessIdsService);
      spyOn(accessIdsService, 'getAccessItemsInformation').and.returnValue(of(new Array<string>(
        'accessID1', 'accessID2'
      )));
      formsValidatorService = TestBed.get(FormsValidatorService);
			component.ngOnChanges({
				active: new SimpleChange(undefined, 'accessItems', true)
			});
			fixture.detectChanges();
			done();
		});
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

	it('should show alert successfull after saving', async(() => {
    fixture.detectChanges();
    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();

    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(alertService.triggerAlert).toHaveBeenCalledWith(
        new AlertModel(AlertType.SUCCESS, `Workbasket  ${component.workbasket.key} Access items were saved successfully`));
    })
    fixture.detectChanges();
  }));

	it('should keep accessItemsClone length to previous value after clearing the form.', () => {
		expect(component.accessItemsClone.length).toBe(2);
		component.remove(1);
		expect(component.accessItemsClone.length).toBe(1);
		component.clear();
		expect(component.accessItemsClone.length).toBe(2);

	});

});
