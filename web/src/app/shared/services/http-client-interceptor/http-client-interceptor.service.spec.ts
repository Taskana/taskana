import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { HttpClientInterceptor } from './http-client-interceptor.service';

describe('HttpExtensionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [HttpClientInterceptor, GeneralModalService, RequestInProgressService]
    });
  });

  it('should be created', inject([HttpClientInterceptor], (service: HttpClientInterceptor) => {
    expect(service).toBeTruthy();
  }));
});
