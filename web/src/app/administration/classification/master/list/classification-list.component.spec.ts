import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';

import { TreeNodeModel } from 'app/models/tree-node';

import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';

import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition/workbasket-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { DomainService } from 'app/services/domain/domain.service';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { Pair } from 'app/models/pair';
import { TreeService } from 'app/services/tree/tree.service';
import { ImportExportService } from 'app/administration/services/import-export/import-export.service';
import { provideMockStore } from '@ngrx/store/testing';
import { ClassificationListComponent } from './classification-list.component';


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
  let classificationsService;
  let classificationCategoriesService;

  beforeEach(done => {
    const initialState = {
      Classification: {
        classificationTypes: ['TASK', 'DOCUMENT'],
        selectedClassificationType: 'DOCUMENT',
        categories: ['EXTERNAL'],
      }
    };
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [ClassificationListComponent, ImportExportComponent, ClassificationTypesSelectorComponent,
          DummyDetailComponent],
        imports: [HttpClientModule, RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule],
        providers: [
          HttpClient, WorkbasketDefinitionService, AlertService, ClassificationsService, DomainService, ClassificationDefinitionService,
          GeneralModalService, RequestInProgressService, ClassificationCategoriesService, TreeService, ImportExportService,
          provideMockStore({ initialState })
        ]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(ClassificationListComponent);
      component = fixture.componentInstance;

      classificationsService = testBed.get(ClassificationsService);
      classificationCategoriesService = testBed.get(ClassificationCategoriesService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(of(treeNodes));
      spyOn(classificationCategoriesService, 'getCategoryIcon').and.returnValue(new Pair('assets/icons/categories/external.svg'));

      fixture.detectChanges();
      done();
    });
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
