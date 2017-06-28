import { TestBed, inject } from '@angular/core/testing';

import { WorkbasketserviceService } from './workbasketservice.service';

describe('WorkbasketserviceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WorkbasketserviceService]
    });
  });

  it('should be created', inject([WorkbasketserviceService], (service: WorkbasketserviceService) => {
    expect(service).toBeTruthy();
  }));
});
