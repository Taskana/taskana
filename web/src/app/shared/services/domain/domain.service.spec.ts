import { TestBed, inject } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';

import { HttpClient, HttpClientModule } from '@angular/common/http';
import { DomainService } from './domain.service';
import { RequestInProgressService } from '../request-in-progress/request-in-progress.service';
import { SelectedRouteService } from '../selected-route/selected-route';
import { StartupService } from '../startup/startup.service';
import { TaskanaEngineService } from '../taskana-engine/taskana-engine.service';
import { WindowRefService } from '../window/window.service';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {}

const routes: Routes = [{ path: '', component: DummyDetailComponent }];

describe('DomainService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule.withRoutes(routes)],
      providers: [
        HttpClient,
        DomainService,
        RequestInProgressService,
        SelectedRouteService,
        StartupService,
        TaskanaEngineService,
        WindowRefService
      ],
      declarations: [DummyDetailComponent]
    });
  });

  it('should be created', inject([DomainService], (service: DomainService) => {
    expect(service).toBeTruthy();
  }));
});
