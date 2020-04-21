import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';
import { NgxsModule, Store } from '@ngxs/store';


import { ClassificationDefinition } from 'app/shared/models/classification-definition';
import { LinksClassification } from 'app/shared/models/links-classfication';

import { MasterAndDetailService } from 'app/shared/services/master-and-detail/master-and-detail.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/shared/models/tree-node';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { TreeService } from 'app/shared/services/tree/tree.service';
import { RemoveConfirmationService } from 'app/shared/services/remove-confirmation/remove-confirmation.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/store/classification-store/classification.selectors';
import { ClassificationDetailsComponent } from './classification-details.component';


@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: 'administration/classifications', component: DummyDetailComponent }
];

describe('ClassificationDetailsComponent', () => {
  let component: ClassificationDetailsComponent;
  let fixture: ComponentFixture<ClassificationDetailsComponent>;
  const treeNodes: Array<TreeNodeModel> = new Array(new TreeNodeModel());

  let classificationsService;
  let treeService;
  let removeConfirmationService;

  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select']);
  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes), AngularSvgIconModule, NgxsModule.forRoot()],
      declarations: [ClassificationDetailsComponent, DummyDetailComponent],
      providers: [MasterAndDetailService, RequestInProgressService, ClassificationsService, HttpClient, GeneralModalService, AlertService,
        TreeService, ImportExportService, { provide: Store, useValue: storeSpy }]
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
      removeConfirmationService = testBed.get(RemoveConfirmationService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(of(treeNodes));
      spyOn(classificationsService, 'deleteClassification').and.returnValue(of(true));
      component.classification = new ClassificationDefinition('id1');
      component.classification._links = new LinksClassification({ self: '' });
      treeService = testBed.get(TreeService);
      fixture.detectChanges();
      done();
    });
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should trigger treeService remove node id after removing a node', () => {
    const treeServiceSpy = spyOn(treeService, 'setRemovedNodeId');
    component.removeClassification();
    removeConfirmationService.runCallbackFunction();
    expect(treeServiceSpy).toHaveBeenCalledWith('id1');
  });
});
