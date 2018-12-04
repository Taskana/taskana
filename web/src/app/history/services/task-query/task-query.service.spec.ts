import { TestBed, inject } from '@angular/core/testing';

import { TaskQueryService } from './task-query.service';

describe('TaskQueryService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TaskQueryService]
    });
  });

  it('should be created', inject([TaskQueryService], (service: TaskQueryService) => {
    expect(service).toBeTruthy();
  }));
});
