import { TestBed } from '@angular/core/testing';

import { TaskFacadeService } from './task-facade.service';

describe.skip('TaskFacadeService', () => {
  let service: TaskFacadeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskFacadeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
