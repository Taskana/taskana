import { TestBed, inject } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';

import { HttpClient, HttpClientModule } from '@angular/common/http';
import { DomainService } from './domain.service';
import { RequestInProgressService } from '../requestInProgress/request-in-progress.service';
import { SelectedRouteService } from '../selected-route/selected-route';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: '', component: DummyDetailComponent }
];

describe('DomainService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        RouterTestingModule.withRoutes(routes)
      ],
      providers: [HttpClient, DomainService, RequestInProgressService, SelectedRouteService],
      declarations: [DummyDetailComponent]
    });
  });

  it('should be created', inject([DomainService], (service: DomainService) => {
    expect(service).toBeTruthy();
  }));
});
