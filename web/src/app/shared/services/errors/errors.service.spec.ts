import { TestBed } from '@angular/core/testing';

import { ErrorsService } from './errors.service';

describe('ErrorsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ErrorsService = TestBed.get(ErrorsService);
    expect(service).toBeTruthy();
  });
});
