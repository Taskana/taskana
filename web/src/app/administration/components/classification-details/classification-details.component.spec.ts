import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';
import { NgxsModule, Store } from '@ngxs/store';
import { Location } from '@angular/common';


import { ClassificationDefinition } from 'app/shared/models/classification-definition';
import { LinksClassification } from 'app/shared/models/links-classfication';

import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/shared/models/tree-node';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { ClassificationDetailsComponent } from './classification-details.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';


@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

describe('ClassificationDetailsComponent', () => {
  let component: ClassificationDetailsComponent;
  let fixture: ComponentFixture<ClassificationDetailsComponent>;
  const treeNodes: Array<TreeNodeModel> = new Array(new TreeNodeModel());

  let classificationsService;
  let removeConfirmationService;

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
      component.classification = new ClassificationDefinition('id1');
      component.classification._links = new LinksClassification({ self: '' });
      fixture.detectChanges();
      done();
    });
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
