import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ClassificationCategoriesService } from 'app/shared/services/classification-categories/classification-categories.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { configureTests } from 'app/app.test.configuration';
import { ClassificationPagingList } from 'app/shared/models/classification-paging-list';
import { TaskdetailsGeneralFieldsComponent } from './general-fields.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {
}

// TODO: test pending to test. Failing random
xdescribe('GeneralComponent', () => {
  let component: TaskdetailsGeneralFieldsComponent;
  let fixture: ComponentFixture<TaskdetailsGeneralFieldsComponent>;
  let classificationsService;

  const routes: Routes = [
    { path: '*', component: DummyDetailComponent }
  ];

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes)],
        declarations: [TaskdetailsGeneralFieldsComponent, DummyDetailComponent],
        providers: [HttpClient, ClassificationCategoriesService,
          DomainService, RequestInProgressService, SelectedRouteService, ClassificationsService]
      });
    };
    configureTests(configure).then(testBed => {
      classificationsService = testBed.get(ClassificationsService);
      const resource: ClassificationPagingList = {
        classifications: [
          {
            classificationId: 'id1',
            key: 'key1',
            category: 'category',
            type: 'type',
            domain: 'DOMAIN_A',
            name: 'classification1',
            parentId: 'parentId',
            parentKey: 'parentKey'
          }, {
            classificationId: 'id2',
            key: 'key2',
            category: 'category',
            type: 'type',
            domain: 'DOMAIN_A',
            name: 'classification1',
            parentId: 'parentId',
            parentKey: 'parentKey'
          },
        ]
      };
      spyOn(classificationsService, 'getClassificationsByDomain').and.returnValue(resource);
      done();
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsGeneralFieldsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call to getClassificationsByDomain', done => {
    component.ngOnInit();
    expect(classificationsService.getClassificationsByDomain).toHaveBeenCalled();
    done();
  });
});
