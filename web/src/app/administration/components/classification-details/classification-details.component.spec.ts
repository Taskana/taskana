import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';
import { NgxsModule, Store } from '@ngxs/store';
import { Location } from '@angular/common';

import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/shared/models/tree-node';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { ClassificationDetailsComponent } from './classification-details.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { ACTION } from '../../../shared/models/action';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

describe('ClassificationDetailsComponent', () => {
  let component: ClassificationDetailsComponent;
  let fixture: ComponentFixture<ClassificationDetailsComponent>;
  const treeNodes: TreeNodeModel[] = [];

  let classificationsService;

  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go']);
  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select', 'dispatch']);
  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, AngularSvgIconModule, NgxsModule.forRoot()],
      declarations: [ClassificationDetailsComponent, DummyDetailComponent],
      providers: [MasterAndDetailService, RequestInProgressService, ClassificationsService,
        HttpClient, NotificationService,
        ImportExportService,
        { provide: Location, useValue: locationSpy },
        { provide: Store, useValue: storeSpy }]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      storeSpy.select.and.callFake(selector => {
        switch (selector) {
          case EngineConfigurationSelectors.classificationsCustomisation:
            return of({ information: {} });
          case ClassificationSelectors.selectCategories:
            return of(['EXTERNAL', 'MANUAL']);
          default:
            return of();
        }
      });

      fixture = testBed.createComponent(ClassificationDetailsComponent);

      component = fixture.componentInstance;
      classificationsService = testBed.get(ClassificationsService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(of(treeNodes));
      spyOn(classificationsService, 'deleteClassification').and.returnValue(of(true));
      component.classification = {
        classificationId: 'CLI:100000000000000000000000000000000002',
        key: 'L10303',
        applicationEntryPoint: '',
        category: 'EXTERNAL',
        domain: 'DOMAIN_A',
        name: 'Beratungsprotokoll',
        parentId: '',
        parentKey: '',
        priority: 101,
        serviceLevel: 'P1D',
        type: 'TASK',
        custom1: 'VNR,RVNR,KOLVNR, ANR',
        custom2: '',
        custom3: '',
        custom4: '',
        custom5: '',
        custom6: '',
        custom7: '',
        custom8: '',
        isValidInDomain: true,
        created: '2020-06-22T12:51:31.164Z',
        modified: '2020-06-22T12:51:31.164Z',
        description: 'Beratungsprotokoll'
      };
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should copy the classification without its key', async done => {
    const startClassification = { ...component.classification };
    const copyButton = fixture.debugElement.nativeElement.querySelector('#copyButton');
    copyButton.click();
    fixture.detectChanges();
    expect(component.classification.name).toEqual(startClassification.name);
    expect(component.classification.classificationId).toEqual(startClassification.classificationId);
    expect(component.classification.key).toBeNull();
    expect(fixture.debugElement.nativeElement.querySelector('#classification-key').disabled).toEqual(false);
    done();
  });

  it('should enable editing of key on create', async done => {
    component.action = ACTION.CREATE;
    await fixture.detectChanges();
    expect(fixture.debugElement.nativeElement.querySelector('#classification-key').disabled).toEqual(false);
    done();
  });
});
