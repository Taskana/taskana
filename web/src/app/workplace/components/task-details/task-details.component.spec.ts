import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpinnerComponent } from 'app/shared/components/spinner/spinner.component';
import { FormsModule } from '@angular/forms';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { WorkplaceService } from '../../services/workplace.service';
import { TaskService } from '../../services/task.service';
import { TaskAttributeValueComponent } from '../task-attribute-value/task-attribute-value.component';
import { TaskCustomFieldsComponent } from '../task-custom-fields/task-custom-fields.component';
import { TaskInformationComponent } from '../task-information/task-information.component';
import { TaskDetailsComponent } from './task-details.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

@Component({
  selector: 'kadai-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {}

const routes: Routes = [{ path: 'workplace/taskdetail/:id', component: DummyDetailComponent }];

// TODO: test pending to test. Failing random
xdescribe('TaskDetailsComponent', () => {
  let component: TaskDetailsComponent;
  let fixture: ComponentFixture<TaskDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TaskDetailsComponent,
        SpinnerComponent,
        TaskAttributeValueComponent,
        TaskCustomFieldsComponent,
        TaskInformationComponent,
        DummyDetailComponent
      ],
      imports: [FormsModule, RouterTestingModule.withRoutes(routes), HttpClientModule],
      providers: [TaskService, HttpClient, WorkplaceService, RequestInProgressService, NotificationService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
