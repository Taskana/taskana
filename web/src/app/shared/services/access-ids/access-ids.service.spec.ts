import { inject, TestBed } from '@angular/core/testing';

import { HttpClientModule } from '@angular/common/http';
import { AccessIdsService } from './access-ids.service';
import { StartupService } from '../startup/startup.service';
import { KadaiEngineService } from '../kadai-engine/kadai-engine.service';
import { WindowRefService } from '../window/window.service';

describe('ValidateAccessItemsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [AccessIdsService, StartupService, KadaiEngineService, WindowRefService]
    });
  });

  it('should be created', inject([AccessIdsService], (service: AccessIdsService) => {
    expect(service).toBeTruthy();
  }));
});
