import { Observable } from 'rxjs/Observable';
import { HttpClient } from '@angular/common/http';
import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ErrorModel } from 'app/models/modal-error';

@Injectable()
export class DomainGuard implements CanActivate {
    constructor(private domainService: DomainService, private errorModalService: ErrorModalService) { }

    canActivate() {
        return this.domainService.getDomains().map(domain => {
            return true;
        }).catch(() => {
            this.errorModalService.triggerError(new ErrorModel(
                'There was an error, please contact with your administrator', 'There was an error getting Domains'))
            return Observable.of(false)
        });
    }
}
