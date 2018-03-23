import { TestBed, inject } from '@angular/core/testing';

import { MasterAndDetailService } from './master-and-detail.service';

describe('MasterAndDetailService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MasterAndDetailService]
    });
  });

  it('should be created', inject([MasterAndDetailService], (service: MasterAndDetailService) => {
    expect(service).toBeTruthy();
  }));
});
