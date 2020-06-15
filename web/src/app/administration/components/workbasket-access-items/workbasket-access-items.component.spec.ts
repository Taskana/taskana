import { SimpleChange } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { of } from 'rxjs';
import { configureTests } from 'app/app.test.configuration';

import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketAccessItems } from 'app/shared/models/workbasket-access-items';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { ICONTYPES } from 'app/shared/models/icon-types';

import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgxsModule, Store } from '@ngxs/store';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { WorkbasketAccessItemsComponent } from './workbasket-access-items.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { AccessItemWorkbasketResource } from '../../../shared/models/access-item-workbasket-resource';

describe('WorkbasketAccessItemsComponent', () => {
  let component: WorkbasketAccessItemsComponent;
  let fixture: ComponentFixture<WorkbasketAccessItemsComponent>;
  let workbasketService;
  let debugElement;
  let notificationsService;
  let accessIdsService;
  let formsValidatorService;

  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [WorkbasketAccessItemsComponent],
      imports: [FormsModule, AngularSvgIconModule, HttpClientModule, ReactiveFormsModule, NgxsModule.forRoot()],
      providers: [WorkbasketService, NotificationService, SavingWorkbasketService, RequestInProgressService,
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

      fixture = testBed.createComponent(WorkbasketAccessItemsComponent);

      component = fixture.componentInstance;
      component.workbasket = new Workbasket('1');
      component.workbasket.type = ICONTYPES.TOPIC;
      component.workbasket._links = { accessItems: { href: 'someurl' } };

      workbasketService = testBed.get(WorkbasketService);
      notificationsService = testBed.get(NotificationService);
      spyOn(workbasketService, 'getWorkBasketAccessItems').and.returnValue(of(new WorkbasketAccessItemsRepresentation(
        new Array<WorkbasketAccessItems>(
          new WorkbasketAccessItems('id1', '1', 'accessID1', '', false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false),
          new WorkbasketAccessItems('id2', '1', 'accessID2')
        ), { self: { href: 'someurl' } }
      )));
      spyOn(workbasketService, 'updateWorkBasketAccessItem').and.returnValue(of(true));
      spyOn(notificationsService, 'showToast').and.returnValue(of(true));
      debugElement = fixture.debugElement.nativeElement;
      accessIdsService = testBed.get(AccessIdsService);
      spyOn(accessIdsService, 'searchForAccessId').and.returnValue(of(['accessID1', 'accessID2']));
      spyOn(accessIdsService, 'getGroupsByAccessId').and.returnValue(of(['accessID1', 'accessID2']));
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

  it('should show success alert after saving', async(() => {
    fixture.detectChanges();
    spyOn(formsValidatorService, 'validateFormAccess').and.returnValue(Promise.resolve(true));
    component.onSubmit();

    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(notificationsService.showToast).toHaveBeenCalledWith(
        NOTIFICATION_TYPES.SUCCESS_ALERT_7,
        new Map<string, string>([['workbasketKey', component.workbasket.key]])
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
