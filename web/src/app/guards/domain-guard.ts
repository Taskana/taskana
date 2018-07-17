import { of } from 'rxjs';
import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ErrorModel } from 'app/models/modal-error';
import { map, catchError } from 'rxjs/operators';

@Injectable()
export class DomainGuard implements CanActivate {
    constructor(private domainService: DomainService, private errorModalService: ErrorModalService) { }

    canActivate() {
        return this.domainService.getDomains().pipe(
            map(domain => {
                return true;
            }),
            catchError(() => {
                this.errorModalService.triggerError(new ErrorModel(
                    'There was an error, please contact with your administrator', 'There was an error getting Domains'))
                return of(false)
            })
        );
    }
}
