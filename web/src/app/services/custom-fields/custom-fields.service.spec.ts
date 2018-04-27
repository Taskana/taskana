import { TestBed, inject } from '@angular/core/testing';

import { CustomFieldsService } from './custom-fields.service';

describe('CustomFieldsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CustomFieldsService]
    });
  });

  it('should be created', inject([CustomFieldsService], (service: CustomFieldsService) => {
    expect(service).toBeTruthy();
  }));
});
