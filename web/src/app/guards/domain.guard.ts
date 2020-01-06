import { of } from 'rxjs';
import { CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { MessageModal } from 'app/models/message-modal';
import { map, catchError } from 'rxjs/operators';

@Injectable()
export class DomainGuard implements CanActivate {
  constructor(private domainService: DomainService, private generalModalService: GeneralModalService) { }

  canActivate() {
    return this.domainService.getDomains().pipe(
      map(domain => true),
      catchError(() => {
        this.generalModalService.triggerMessage(new MessageModal(
          'There was an error, please contact with your administrator', 'There was an error getting Domains'
        ));
        return of(false)
      })
    );
  }
}
