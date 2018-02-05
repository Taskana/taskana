import { TestBed, inject } from '@angular/core/testing';

import { WorkbasketService } from './workbasketservice.service';

describe('WorkbasketService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WorkbasketService]
    });
  });

  xit('should be created', inject([WorkbasketService], (service: WorkbasketService) => {
    expect(service).toBeTruthy();
  }));
});
