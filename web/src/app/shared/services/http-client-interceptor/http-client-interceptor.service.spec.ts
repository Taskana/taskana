import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Overlay } from '@angular/cdk/overlay';

import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { MAT_DIALOG_SCROLL_STRATEGY, MatDialog } from '@angular/material/dialog';
import { HttpClientInterceptor } from './http-client-interceptor.service';
import { NotificationService } from '../notifications/notification.service';

describe('HttpExtensionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [HttpClientInterceptor, RequestInProgressService, MatSnackBar, Overlay, MatDialog, { provide: MAT_DIALOG_SCROLL_STRATEGY }]
    });
  });

  it('should be created', inject([HttpClientInterceptor], (service: HttpClientInterceptor) => {
    expect(service).toBeTruthy();
  }));
});
