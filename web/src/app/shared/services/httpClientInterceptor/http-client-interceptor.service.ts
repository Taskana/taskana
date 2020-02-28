import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { MessageModal } from 'app/models/message-modal';

import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { environment } from 'environments/environment';
import { tap } from 'rxjs/operators';
import { ERROR_TYPES } from '../../../services/general-modal/errors';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {
  constructor(
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService
  ) {

  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let req = request.clone({ headers: request.headers.set('Content-Type', 'application/hal+json') });
    if (!environment.production) {
      req = req.clone({ headers: req.headers.set('Authorization', 'Basic YWRtaW46YWRtaW4=') });
    }
    return next.handle(req).pipe(tap(() => { }, error => {
      this.requestInProgressService.setRequestInProgress(false);
      if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
        // new Key ERROR_TYPES.ACCESS_ERR
        this.generalModalService.triggerMessage(
          new MessageModal('You have no access to this resource ', error)
        );
      } else if (error instanceof HttpErrorResponse && (error.status === 404) && error.url.indexOf('environment-information.json')) {
        // ignore this error message Key ERROR_TYPES.NONE
      } else {
        // new Key ERROR_TYPES.GENERAL_ERR
        this.generalModalService.triggerMessage(
          new MessageModal('There was error, please contact with your administrator ', error)
        );
      }
    }));
  }
}
