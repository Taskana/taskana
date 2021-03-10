import { TestBed, inject, getTestBed } from '@angular/core/testing';

import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StartupService } from './startup.service';
import { TaskanaEngineService } from '../taskana-engine/taskana-engine.service';
import { WindowRefService } from '../window/window.service';
import { environment } from '../../../../environments/environment';

describe('StartupService', () => {
  const environmentFile = 'environments/data-sources/environment-information.json';
  const someRestUrl = 'someRestUrl';
  const someLogoutUrl = 'someLogoutUrl';
  const dummyEnvironmentInformation = {
    taskanaRestUrl: someRestUrl,
    taskanaLogoutUrl: someLogoutUrl
  };

  let httpMock;
  let service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [StartupService, HttpClient, TaskanaEngineService, WindowRefService]
    });
  });

  beforeEach(() => {
    const injector = getTestBed();
    httpMock = injector.inject(HttpTestingController);
    // UserService provided to the TestBed
    service = injector.inject(StartupService);
  });

  it('should be created', inject([StartupService], () => {
    expect(service).toBeTruthy();
  }));

  it('should initialize rest and logout url from external file', (done) => {
    environment.taskanaRestUrl = '';
    environment.taskanaLogoutUrl = '';
    service.getEnvironmentFilePromise().then((res) => {
      expect(environment.taskanaRestUrl).toBe(someRestUrl);
      expect(environment.taskanaLogoutUrl).toBe(someLogoutUrl);
      done();
    });
    const req = httpMock.expectOne(environmentFile);
    expect(req.request.method).toBe('GET');
    req.flush(dummyEnvironmentInformation);
    httpMock.verify();
  });

  it('should initialize rest and logout url from external file and override previous config', (done) => {
    environment.taskanaRestUrl = 'oldRestUrl';
    environment.taskanaLogoutUrl = 'oldLogoutUrl';
    service.getEnvironmentFilePromise().then((res) => {
      expect(environment.taskanaRestUrl).toBe(someRestUrl);
      expect(environment.taskanaLogoutUrl).toBe(someLogoutUrl);
      done();
    });
    const req = httpMock.expectOne(environmentFile);
    expect(req.request.method).toBe('GET');
    req.flush(dummyEnvironmentInformation);
    httpMock.verify();
  });
});
