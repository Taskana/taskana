import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { environment } from 'environments/environment';
import { tap } from 'rxjs/operators';
import { NotificationService } from '../notifications/notification.service';
import { NOTIFICATION_TYPES } from '../../models/notifications';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {
  constructor(private requestInProgressService: RequestInProgressService, private errorsService: NotificationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let req = request.clone({ headers: request.headers.set('Content-Type', 'application/hal+json') });
    if (!environment.production) {
      req = req.clone({ headers: req.headers.set('Authorization', 'Basic YWRtaW46YWRtaW4=') });
    }
    return next.handle(req).pipe(
      tap(
        () => {},
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
            this.errorsService.triggerError(NOTIFICATION_TYPES.ACCESS_ERR, error);
          } else if (
            error instanceof HttpErrorResponse &&
            error.status === 404 &&
            error.url.indexOf('environment-information.json')
          ) {
            // ignore this error
          } else if (
            (error.status === 409 && error.error.exception.endsWith('WorkbasketAccessItemAlreadyExistException')) ||
            error.error.exception.endsWith('WorkbasketAlreadyExistException') ||
            error.error.exception.endsWith('ClassificationAlreadyExistException')
          ) {
            return;
          } else {
            this.errorsService.triggerError(NOTIFICATION_TYPES.GENERAL_ERR, error);
          }
        }
      )
    );
  }
}
