import { of } from 'rxjs';
import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { catchError, map } from 'rxjs/operators';
import { ErrorsService } from '../services/errors/errors.service';
import { ERROR_TYPES } from '../models/errors';

@Injectable()
export class DomainGuard implements CanActivate {
  constructor(private domainService: DomainService, private errorsService: ErrorsService) {
  }

  canActivate() {
    return this.domainService.getDomains().pipe(
      map(domain => true),
      catchError(() => {
        this.errorsService.updateError(ERROR_TYPES.FETCH_ERR_5);
        return of(false);
      })
    );
  }
}
