import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { HttpClientInterceptor } from './http-client-interceptor.service';
import { PermissionService } from './permission.service';

describe('HttpExtensionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientModule, HttpModule],
      providers: [HttpClientInterceptor, PermissionService]
    });
  });

  it('should be created', inject([HttpClientInterceptor], (service: HttpClientInterceptor) => {
    expect(service).toBeTruthy();
  }));
});
