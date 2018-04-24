import { TestBed, inject } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { ClassificationCategoriesService } from './classification-categories.service';

describe('CategoryService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [HttpClient, ClassificationCategoriesService]
    });
  });

  it('should be created', inject([ClassificationCategoriesService], (service: ClassificationCategoriesService) => {
    expect(service).toBeTruthy();
  }));
});
