import { TestBed, inject } from '@angular/core/testing';

import { TitlesService } from './titles.service';

describe('TitlesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TitlesService]
    });
  });

  it('should be created', inject([TitlesService], (service: TitlesService) => {
    expect(service).toBeTruthy();
  }));
});
