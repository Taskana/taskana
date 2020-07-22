import { of } from 'rxjs';
import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { catchError, map } from 'rxjs/operators';
import { NotificationService } from '../services/notifications/notification.service';
import { NOTIFICATION_TYPES } from '../models/notifications';

@Injectable()
export class DomainGuard implements CanActivate {
  constructor(private domainService: DomainService, private errorsService: NotificationService) {}

  canActivate() {
    return this.domainService.getDomains().pipe(
      map((domain) => true),
      catchError(() => {
        this.errorsService.triggerError(NOTIFICATION_TYPES.FETCH_ERR_5);
        return of(false);
      })
    );
  }
}
