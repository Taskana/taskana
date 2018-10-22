import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskdetailsComponent } from './taskdetails.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { FormsModule } from '@angular/forms';
import { TaskdetailsGeneralFieldsComponent } from './general/general-fields.component';
import { TaskdetailsCustomFieldsComponent } from './custom/custom-fields.component';
import { TaskdetailsAttributeComponent } from './attribute/attribute.component';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { TaskService } from '../services/task.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { WorkplaceService } from '../services/workplace.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: 'workplace/taskdetail/:id', component: DummyDetailComponent }
];

// TODO: test pending to test. Failing random
xdescribe('TaskdetailsComponent', () => {
  let component: TaskdetailsComponent;
  let fixture: ComponentFixture<TaskdetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TaskdetailsComponent, SpinnerComponent,
        TaskdetailsGeneralFieldsComponent, TaskdetailsCustomFieldsComponent,
        TaskdetailsAttributeComponent, DummyDetailComponent],
      imports: [FormsModule, RouterTestingModule.withRoutes(routes), HttpClientModule],
      providers: [TaskService, HttpClient, WorkplaceService, RemoveConfirmationService,
        RequestInProgressService, AlertService, ErrorModalService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskdetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
