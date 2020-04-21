import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpinnerComponent } from 'app/shared/components/spinner/spinner.component';
import { FormsModule } from '@angular/forms';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RemoveConfirmationService } from 'app/shared/services/remove-confirmation/remove-confirmation.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { WorkplaceService } from '../services/workplace.service';
import { TaskService } from '../services/task.service';
import { TaskdetailsAttributeComponent } from './attribute/attribute.component';
import { TaskdetailsCustomFieldsComponent } from './custom/custom-fields.component';
import { TaskdetailsGeneralFieldsComponent } from './general/general-fields.component';
import { TaskdetailsComponent } from './taskdetails.component';

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
        RequestInProgressService, AlertService, GeneralModalService]
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
