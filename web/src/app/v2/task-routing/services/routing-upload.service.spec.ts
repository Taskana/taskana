import { TestBed } from '@angular/core/testing';

import { RoutingUploadService } from './routing-upload.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StartupService } from 'app/shared/services/startup/startup.service';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';
import { WindowRefService } from 'app/shared/services/window/window.service';

describe('RoutingUploadService', () => {
  let service: RoutingUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [StartupService, KadaiEngineService, WindowRefService]
    });
    service = TestBed.inject(RoutingUploadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
