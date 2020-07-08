import { Component, SimpleChange } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { of } from 'rxjs';
import { configureTests } from 'app/app.test.configuration';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Location } from '@angular/common';
import { SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgxsModule, Store } from '@ngxs/store';
import { WorkbasketAccessItemsComponent } from './workbasket-access-items.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { EngineConfigurationState } from '../../../shared/store/engine-configuration-store/engine-configuration.state';
import { ClassificationCategoriesService } from '../../../shared/services/classification-categories/classification-categories.service';

describe('WorkbasketAccessItemsComponent', () => {
  let component: WorkbasketAccessItemsComponent;
  let fixture: ComponentFixture<WorkbasketAccessItemsComponent>;
  let workbasketService;
  let debugElement;
  let notificationsService;
  let accessIdsService;
  let formsValidatorService;
  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [WorkbasketAccessItemsComponent],
      imports: [
        FormsModule, AngularSvgIconModule,
        HttpClientModule, ReactiveFormsModule,
        NgxsModule.forRoot([WorkbasketState, EngineConfigurationState])],
      providers: [WorkbasketService, NotificationService, SavingWorkbasketService, RequestInProgressService,
        AccessIdsService, FormsValidatorService, ClassificationCategoriesService,
        { provide: Location, useValue: locationSpy },
      ]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      const store: Store = testBed.get(Store);
      store.reset([WorkbasketState, EngineConfigurationState]);

      fixture = testBed.createComponent(WorkbasketAccessItemsComponent);

      component = fixture.componentInstance;
      component.workbasket = { type: ICONTYPES.PERSONAL };
      component.workbasket.type = ICONTYPES.TOPIC;
      component.workbasket._links = { accessItems: { href: 'someurl' } };

      workbasketService = testBed.get(WorkbasketService);
      notificationsService = testBed.get(NotificationService);
      const workbasketAccessItemsRepresentation: WorkbasketAccessItemsRepresentation = {
        accessItems: [{
          accessId: 'accessID1',
          workbasketId: 'id1',
          workbasketKey: '1',
          accessItemId: '',
          accessName: '',
          permRead: false,
          permOpen: false,
          permAppend: false,
          permTransfer: false,
          permDistribute: false,
          permCustom1: false,
          permCustom2: false,
          permCustom3: false,
          permCustom4: false,
          permCustom5: false,
          permCustom6: false,
          permCustom7: false,
          permCustom8: false,
          permCustom9: false,
          permCustom10: false,
          permCustom11: false,
          permCustom12: false,
          _links: {},
        }],
        _links: { self: { href: 'someurl' } }
      };
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
    fixture.destroy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
