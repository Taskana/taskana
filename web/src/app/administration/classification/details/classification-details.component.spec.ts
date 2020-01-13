import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { of } from 'rxjs';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';


import { ClassificationDefinition } from 'app/models/classification-definition';
import { LinksClassification } from 'app/models/links-classfication';
import { Pair } from 'app/models/pair';

// tslint:disable:max-line-length
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
// tslint:enable:max-line-length
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/models/tree-node';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { AlertService } from 'app/services/alert/alert.service';
import { TreeService } from 'app/services/tree/tree.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { ImportExportService } from 'app/administration/services/import-export/import-export.service';
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

  let classificationsService; let classificationCategoriesService;
    let treeService; let
removeConfirmationService;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes), AngularSvgIconModule],
        declarations: [ClassificationDetailsComponent, DummyDetailComponent],
        providers: [MasterAndDetailService, RequestInProgressService, ClassificationsService, HttpClient, GeneralModalService, AlertService,
          TreeService, ClassificationCategoriesService, CustomFieldsService, ImportExportService]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(ClassificationDetailsComponent);
      component = fixture.componentInstance;
      classificationsService = TestBed.get(ClassificationsService);
      classificationCategoriesService = TestBed.get(ClassificationCategoriesService);
      removeConfirmationService = TestBed.get(RemoveConfirmationService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(of(treeNodes));
      spyOn(classificationCategoriesService, 'getClassificationTypes').and.returnValue(of([]));
      spyOn(classificationCategoriesService, 'getCategories').and.returnValue(of(['firstCategory', 'secondCategory']));
      spyOn(classificationsService, 'deleteClassification').and.returnValue(of(true));
      spyOn(classificationCategoriesService, 'getCategoryIcon').and.returnValue(new Pair('assets/icons/categories/external.svg'));
      component.classification = new ClassificationDefinition('id1');
      component.classification._links = new LinksClassification({ self: '' });
      treeService = TestBed.get(TreeService);
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

  it('should selected first classificationCategory if is defined', () => {
    expect(component.classification.category).toBe('firstCategory');
  });
});
