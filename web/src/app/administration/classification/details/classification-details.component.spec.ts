import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AppModule } from 'app/app.module'

import { ClassificationDetailsComponent } from './classification-details.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { ClassificationDefinition } from 'app/models/classification-definition';
import { LinksClassification } from 'app/models/links-classfication';

import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { ClassificationsService } from 'app/administration/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/models/tree-node';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { AlertService } from 'app/services/alert/alert.service';
import { TreeService } from 'app/services/tree/tree.service';
import { ClassificationTypesService } from 'app/administration/services/classification-types/classification-types.service';
// tslint:disable:max-line-length
import { ClassificationCategoriesService } from 'app/administration/services/classification-categories-service/classification-categories.service';
// tslint:enable:max-line-length
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { configureTests } from 'app/app.test.configuration';

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
  const classificationTypes: Array<string> = new Array<string>('type1', 'type2');
  let classificationsSpy, classificationsTypesSpy;
  let classificationsService, classificationTypesService, classificationCategoriesService;
  let treeService;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes), AngularSvgIconModule],
        declarations: [ClassificationDetailsComponent, SpinnerComponent, DummyDetailComponent],
        providers: [MasterAndDetailService, RequestInProgressService, ClassificationsService, HttpClient, ErrorModalService, AlertService,
          TreeService, ClassificationTypesService, ClassificationCategoriesService,
          CustomFieldsService]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(ClassificationDetailsComponent);
      component = fixture.componentInstance;
      classificationsService = TestBed.get(ClassificationsService);
      classificationTypesService = TestBed.get(ClassificationTypesService);
      classificationCategoriesService = TestBed.get(ClassificationCategoriesService);
      classificationsSpy = spyOn(classificationsService, 'getClassifications').and.returnValue(Observable.of(treeNodes));
      classificationsTypesSpy = spyOn(classificationTypesService, 'getClassificationTypes').and.returnValue(Observable.of([]));
      spyOn(classificationCategoriesService, 'getCategories').and.returnValue(Observable.of(['firstCategory', 'secondCategory']));
      spyOn(classificationsService, 'deleteClassification').and.returnValue(Observable.of(true));
      component.classification = new ClassificationDefinition('id1', undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, new LinksClassification({ 'self': '' }));
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
    expect(treeServiceSpy).toHaveBeenCalledWith('id1');
  });

  it('should selected first classificationCategory if is defined', () => {
    expect(component.classification.category).toBe('firstCategory');
  });
});
