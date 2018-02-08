import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { HttpExtensionService } from './http-extension.service';
import { PermissionService } from './permission.service';

describe('HttpExtensionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientModule, HttpModule],
      providers: [HttpExtensionService, PermissionService]
    });
  });

  it('should be created', inject([HttpExtensionService], (service: HttpExtensionService) => {
    expect(service).toBeTruthy();
  }));
});
