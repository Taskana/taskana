import { TestBed, inject } from '@angular/core/testing';

import { AccessIdsService } from './access-ids.service';
import { HttpClientModule } from '@angular/common/http';

describe('ValidateAccessItemsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [AccessIdsService]
    });
  });

  it('should be created', inject([AccessIdsService], (service: AccessIdsService) => {
    expect(service).toBeTruthy();
  }));
});
