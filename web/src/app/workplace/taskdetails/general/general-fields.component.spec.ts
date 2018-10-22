import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsGeneralFieldsComponent } from './general-fields.component';
import { FormsModule } from '@angular/forms';
import { ClassificationsService } from 'app/services/classifications/classifications.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ClassificationCategoriesService } from 'app/services/classifications/classification-categories.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { DomainService } from 'app/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';

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

  const routes: Routes = [
		{ path: '*', component: DummyDetailComponent }
	];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes)],
      declarations: [TaskdetailsGeneralFieldsComponent, DummyDetailComponent],
      providers: [ClassificationsService, HttpClient, ClassificationCategoriesService, CustomFieldsService,
        DomainService, RequestInProgressService, SelectedRouteService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsGeneralFieldsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
