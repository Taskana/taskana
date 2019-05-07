import {TestBed, inject, getTestBed} from '@angular/core/testing';

import {StartupService} from './startup.service'
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {CustomFieldsService} from '../custom-fields/custom-fields.service';
import {TaskanaEngineService} from '../taskana-engine/taskana-engine.service';
import {WindowRefService} from '../window/window.service';
import {environment} from '../../../environments/environment';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

describe('StartupService', () => {
  const environmentFile = 'environments/data-sources/environment-information.json';
  const someRestUrl = 'someRestUrl';
  const someLogoutUrl = 'someLogoutUrl';
  const dummyEnvironmentInformation = {
    'taskanaRestUrl': someRestUrl,
    'taskanaLogoutUrl': someLogoutUrl
  };

  let httpMock, service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule
      ],
      providers: [
        StartupService,
        HttpClient,
        CustomFieldsService,
        TaskanaEngineService,
        WindowRefService
      ]
    });
  });

  beforeEach(() => {
    const injector = getTestBed();
    httpMock = injector.get(HttpTestingController);
    // UserService provided to the TestBed
    service = TestBed.get(StartupService);
  })

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
    })
    const req = httpMock.expectOne(environmentFile);
    expect(req.request.method).toBe('GET');
    req.flush(dummyEnvironmentInformation);
    httpMock.verify();
  });
});
