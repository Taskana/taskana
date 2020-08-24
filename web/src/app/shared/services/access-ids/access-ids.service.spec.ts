import { TestBed, inject } from '@angular/core/testing';

import { HttpClientModule } from '@angular/common/http';
import { AccessIdsService } from './access-ids.service';
import { SelectedRouteService } from '../selected-route/selected-route';
import { StartupService } from '../startup/startup.service';
import { TaskanaEngineService } from '../taskana-engine/taskana-engine.service';
import { WindowRefService } from '../window/window.service';

describe('ValidateAccessItemsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [AccessIdsService, StartupService, TaskanaEngineService, WindowRefService]
    });
  });

  it('should be created', inject([AccessIdsService], (service: AccessIdsService) => {
    expect(service).toBeTruthy();
  }));
});
