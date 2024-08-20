import { TestBed, inject, getTestBed } from '@angular/core/testing';

import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StartupService } from './startup.service';
import { KadaiEngineService } from '../kadai-engine/kadai-engine.service';
import { WindowRefService } from '../window/window.service';
import { environment } from '../../../../environments/environment';

describe('StartupService', () => {
  const environmentFile = 'environments/data-sources/environment-information.json';
  const someRestUrl = 'someRestUrl';
  const someLogoutUrl = 'someLogoutUrl';
  const dummyEnvironmentInformation = {
    kadaiRestUrl: someRestUrl,
    kadaiLogoutUrl: someLogoutUrl
  };

  let httpMock;
  let service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [StartupService, HttpClient, KadaiEngineService, WindowRefService]
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
    environment.kadaiRestUrl = '';
    environment.kadaiLogoutUrl = '';
    service.getEnvironmentFilePromise().then((res) => {
      expect(environment.kadaiRestUrl).toBe(someRestUrl);
      expect(environment.kadaiLogoutUrl).toBe(someLogoutUrl);
      done();
    });
    const req = httpMock.expectOne(environmentFile);
    expect(req.request.method).toBe('GET');
    req.flush(dummyEnvironmentInformation);
    httpMock.verify();
  });

  it('should initialize rest and logout url from external file and override previous config', (done) => {
    environment.kadaiRestUrl = 'oldRestUrl';
    environment.kadaiLogoutUrl = 'oldLogoutUrl';
    service.getEnvironmentFilePromise().then((res) => {
      expect(environment.kadaiRestUrl).toBe(someRestUrl);
      expect(environment.kadaiLogoutUrl).toBe(someLogoutUrl);
      done();
    });
    const req = httpMock.expectOne(environmentFile);
    expect(req.request.method).toBe('GET');
    req.flush(dummyEnvironmentInformation);
    httpMock.verify();
  });
});
