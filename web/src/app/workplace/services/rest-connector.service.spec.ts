import { TestBed, inject } from '@angular/core/testing';

import { RestConnectorService } from './rest-connector.service';

describe('RestConnectorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RestConnectorService]
    });
  });

  it('should be created', inject([RestConnectorService], (service: RestConnectorService) => {
    expect(service).toBeTruthy();
  }));
});
