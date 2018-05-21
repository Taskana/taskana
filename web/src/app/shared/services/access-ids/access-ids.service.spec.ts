import { TestBed, inject } from '@angular/core/testing';

import { AccessIdsService } from './access-ids.service';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

describe('ValidateAccessItemsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpModule],
      providers: [AccessIdsService]
    });
  });

  it('should be created', inject([AccessIdsService], (service: AccessIdsService) => {
    expect(service).toBeTruthy();
  }));
});
