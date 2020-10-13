import { TestBed, inject } from '@angular/core/testing';

import { SidenavService } from './sidenav.service';

describe('SidenavService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SidenavService]
    });
  });

  it('should be created', inject([SidenavService], (service: SidenavService) => {
    expect(service).toBeTruthy();
  }));
});
