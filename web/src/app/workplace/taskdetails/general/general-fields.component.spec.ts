import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { DomainService } from 'app/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { configureTests } from 'app/app.test.configuration';
import { ClassificationResource } from 'app/models/classification-resource';
import { Classification } from 'app/models/classification';
import { Links } from 'app/models/links';
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
      TestBed.configureTestingModule({
        imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes)],
        declarations: [TaskdetailsGeneralFieldsComponent, DummyDetailComponent],
        providers: [HttpClient, ClassificationCategoriesService,
          DomainService, RequestInProgressService, SelectedRouteService, ClassificationsService]
      });
    };
    configureTests(configure).then(testBed => {
      classificationsService = TestBed.get(ClassificationsService);
      spyOn(classificationsService, 'getClassificationsByDomain').and.returnValue(new ClassificationResource(
        new Array<Classification>(
          new Classification('id1', '1', 'category', 'type', 'domain_a', 'classification1', 'parentId',
            1, 'service', new Links({ href: 'someurl' })),
          new Classification('id2', '2', 'category', 'type', 'domain_a', 'classification2', 'parentId2',
            1, 'service', new Links({ href: 'someurl' }))
        ),
        new Links({ href: 'someurl' })
      ));
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
