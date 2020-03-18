import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { environment } from 'environments/environment';
import { tap } from 'rxjs/operators';
import { ErrorsService } from '../../../services/errors/errors.service';
import { ERROR_TYPES } from '../../../models/errors';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {
  constructor(
    private requestInProgressService: RequestInProgressService,
    private errorsService: ErrorsService
  ) {

  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let req = request.clone({ headers: request.headers.set('Content-Type', 'application/hal+json') });
    if (!environment.production) {
      req = req.clone({ headers: req.headers.set('Authorization', 'Basic YWRtaW46YWRtaW4=') });
    }
    return next.handle(req).pipe(tap(() => {
    }, error => {
      this.requestInProgressService.setRequestInProgress(false);
      if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
        this.errorsService.updateError(ERROR_TYPES.ACCESS_ERR, error);
      } else if (error instanceof HttpErrorResponse && (error.status === 404) && error.url.indexOf('environment-information.json')) {
        // ignore this error
      } else {
        this.errorsService.updateError(ERROR_TYPES.GENERAL_ERR, error);
      }
    }));
  }
}
