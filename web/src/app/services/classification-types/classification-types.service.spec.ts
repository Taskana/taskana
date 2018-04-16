import { TestBed, inject } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { ClassificationTypesService } from './classification-types.service';

describe('ClassificationTypesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [HttpClient, ClassificationTypesService]
    });
  });

  it('should be created', inject([ClassificationTypesService], (service: ClassificationTypesService) => {
    expect(service).toBeTruthy();
  }));
});
