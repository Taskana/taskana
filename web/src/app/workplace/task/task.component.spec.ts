import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Routes } from '@angular/router';

import { SpinnerComponent } from 'app/shared/components/spinner/spinner.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Component } from '@angular/core';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { TaskService } from '../services/task.service';
import { TaskComponent } from './task.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: 'workplace/tasks', component: DummyDetailComponent }
];

// TODO: test pending to test. Failing random
xdescribe('TaskComponent', () => {
  let component: TaskComponent;
  let fixture: ComponentFixture<TaskComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes)],
      declarations: [TaskComponent, SpinnerComponent, DummyDetailComponent],
      providers: [TaskService, HttpClient, WorkbasketService, DomainService, RequestInProgressService,
        SelectedRouteService, GeneralModalService, ClassificationsService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
