import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';

import { TreeNodeModel } from 'app/shared/models/tree-node';

import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';
import { ClassificationTypesSelectorComponent } from 'app/administration/components/classification-types-selector/classification-types-selector.component';

import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { configureTests } from 'app/app.test.configuration';
import { TreeService } from 'app/shared/services/tree/tree.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NgxsModule } from '@ngxs/store';
import { ClassificationListComponent } from './classification-list.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';


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

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      declarations: [ClassificationListComponent, ImportExportComponent, ClassificationTypesSelectorComponent,
        DummyDetailComponent],
      imports: [HttpClientModule, RouterTestingModule.withRoutes(routes), FormsModule, AngularSvgIconModule, NgxsModule.forRoot()],
      providers: [
        HttpClient, WorkbasketDefinitionService, NotificationService,
        ClassificationsService, DomainService, ClassificationDefinitionService,
        GeneralModalService, RequestInProgressService, TreeService, ImportExportService
      ]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(ClassificationListComponent);
      component = fixture.componentInstance;

      classificationsService = testBed.get(ClassificationsService);
      spyOn(classificationsService, 'getClassifications').and.returnValue(of(treeNodes));

      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
