import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Routes } from '@angular/router';

import { TaskComponent } from './task.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TaskService } from '../services/task.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { Component } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';

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
        SelectedRouteService, GeneralModalService]
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
