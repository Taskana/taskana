import { TestBed } from '@angular/core/testing';

import { RoutingUploadService } from './routing-upload.service';

describe('RoutingUploadService', () => {
  let service: RoutingUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoutingUploadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
