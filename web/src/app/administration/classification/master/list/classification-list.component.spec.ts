import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationListComponent } from './classification-list.component';
import { ImportExportComponent } from 'app/shared/import-export/import-export.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { WorkbasketDefinitionService } from 'app/services/workbasket-definition/workbasket-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationsService } from 'app/services/classifications/classifications.service';
import { ClassificationDefinitionService } from 'app/services/classification-definition/classification-definition.service';
import { DomainService } from 'app/services/domains/domain.service';

@Component({
  selector: 'taskana-tree',
  template: ''
})
class TaskanaTreeComponent {
  @Input() treeNodes;
  @Input() selectNodeId;
}

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: ':id', component: DummyDetailComponent, outlet: 'detail' },
  { path: 'classifications', component: DummyDetailComponent }
];


describe('ClassificationListComponent', () => {
  let component: ClassificationListComponent;
  let fixture: ComponentFixture<ClassificationListComponent>;
  const treeNodes: Array<TreeNodeModel> = new Array(new TreeNodeModel());
  const classificationTypes: Array<string> = new Array<string>('type1', 'type2');
  let classificationsSpy, classificationsTypesSpy;
  let classificationsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationListComponent, ImportExportComponent, SpinnerComponent, ClassificationTypesSelectorComponent,
        TaskanaTreeComponent, DummyDetailComponent],
      imports: [HttpClientModule, RouterTestingModule.withRoutes(routes)],
      providers: [
        HttpClient, WorkbasketDefinitionService, AlertService, ClassificationsService, DomainService, ClassificationDefinitionService

      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationListComponent);
    component = fixture.componentInstance;
    classificationsService = TestBed.get(ClassificationsService);
    classificationsSpy = spyOn(classificationsService, 'getClassifications').and.returnValue(Observable.of(treeNodes));
    classificationsTypesSpy = spyOn(classificationsService, 'getClassificationTypes').and.returnValue(Observable.of(classificationTypes));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
