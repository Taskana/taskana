import { TestBed, inject } from '@angular/core/testing';
import { OrientationService } from './orientation.service';

describe('OrientationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrientationService]
    });
  });

  it('should be created', inject([OrientationService], (service: OrientationService) => {
    expect(service).toBeTruthy();
  }));
});
