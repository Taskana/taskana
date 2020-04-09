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


import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { NgxsModule, Store } from '@ngxs/store';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { AccessItemsComponent } from './access-items.component';

describe('AccessItemsComponent', () => {
  let component: AccessItemsComponent;
  let fixture: ComponentFixture<AccessItemsComponent>;
  let workbasketService;
  let debugElement;
  let alertService;
  let accessIdsService;
  let formsValidatorService;

  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [AccessItemsComponent],
      imports: [FormsModule, AngularSvgIconModule, HttpClientModule, ReactiveFormsModule, NgxsModule.forRoot()],
      providers: [WorkbasketService, AlertService, GeneralModalService, SavingWorkbasketService, RequestInProgressService,
        AccessIdsService, FormsValidatorService, { provide: Store, useValue: storeSpy }]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      storeSpy.select.and.callFake(selector => {
        switch (selector) {
          case EngineConfigurationSelectors.accessItemsCustomisation:
            return of({
              accessId: {
                lookupField: false
              },
              custom1: {}
            });
          default:
            return of();
        }
      });

      fixture = testBed.createComponent(AccessItemsComponent);

      component = fixture.componentInstance;
      component.workbasket = new Workbasket('1');
      component.workbasket.type = ICONTYPES.TOPIC;
      component.workbasket._links = new Links();
      component.workbasket._links.accessItems = { href: 'someurl' };

      workbasketService = testBed.get(WorkbasketService);
      alertService = testBed.get(AlertService);
      spyOn(workbasketService, 'getWorkBasketAccessItems').and.returnValue(of(new WorkbasketAccessItemsResource(
        new Array<WorkbasketAccessItems>(
          new WorkbasketAccessItems('id1', '1', 'accessID1', '', false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false),
          new WorkbasketAccessItems('id2', '1', 'accessID2')
        ),
        new Links({ href: 'someurl' })
      )));
      spyOn(workbasketService, 'updateWorkBasketAccessItem').and.returnValue(of(true));
      spyOn(alertService, 'triggerAlert').and.returnValue(of(true));
      debugElement = fixture.debugElement.nativeElement;
      accessIdsService = testBed.get(AccessIdsService);
      spyOn(accessIdsService, 'getAccessItemsInformation').and.returnValue(of(new Array<string>(
        'accessID1', 'accessID2'
      )));
      formsValidatorService = testBed.get(FormsValidatorService);
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
        new AlertModel(AlertType.SUCCESS, `Workbasket  ${component.workbasket.key} Access items were saved successfully`)
      );
    });
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
