import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpModule } from '@angular/http';

import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationListComponent } from './classification-list.component';
import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { MapValuesPipe } from 'app/shared/pipes/mapValues/map-values.pipe';

import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition/workbasket-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationsService } from 'app/administration/services/classifications/classifications.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ClassificationTypesService } from 'app/administration/services/classification-types/classification-types.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';
import {
  ClassificationCategoriesService
} from 'app/administration/services/classification-categories-service/classification-categories.service';
import { Pair } from 'app/models/pair';

@Component({
  selector: 'taskana-tree',
  template: ''
})
class TaskanaTreeComponent {
  @Input() treeNodes;
  @Input() selectNodeId;
  @Input() filterText;
  @Input() filterIcon;
}

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [

  { path: ':id', component: DummyDetailComponent }
];


describe('ClassificationListComponent', () => {
  let component: ClassificationListComponent;
  let fixture: ComponentFixture<ClassificationListComponent>;
  const treeNodes: Array<TreeNodeModel> = new Array(new TreeNodeModel());
  const classificationTypes: Array<string> = new Array<string>('type1', 'type2');
  let classificationsService, classificationTypesService, classificationCategoriesService;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [ClassificationListComponent, ImportExportComponent, SpinnerComponent, ClassificationTypesSelectorComponent,
          TaskanaTreeComponent, DummyDetailComponent, IconTypeComponent, MapValuesPipe],
        imports: [HttpClientModule, RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, HttpModule],
        providers: [
          HttpClient, WorkbasketDefinitionService, AlertService, ClassificationsService, DomainService, ClassificationDefinitionService,
          ErrorModalService, ClassificationTypesService, RequestInProgressService, ClassificationCategoriesService
        ]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(ClassificationListComponent);
      component = fixture.componentInstance;

      classificationsService = testBed.get(ClassificationsService);
      classificationTypesService = testBed.get(ClassificationTypesService);
      classificationCategoriesService = testBed.get(ClassificationCategoriesService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(Observable.of(treeNodes));
      spyOn(classificationTypesService, 'getClassificationTypes')
        .and.returnValue(Observable.of(classificationTypes));
      spyOn(classificationCategoriesService, 'getCategories').and.returnValue(Observable.of(new Array<string>('cat1', 'cat2')));
      spyOn(classificationCategoriesService, 'getCategoryIcon').and.returnValue(new Pair('assets/icons/categories/external.svg'));
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
