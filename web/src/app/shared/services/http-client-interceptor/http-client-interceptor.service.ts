import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpXsrfTokenExtractor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { environment } from 'environments/environment';
import { tap } from 'rxjs/operators';
import { NotificationService } from '../notifications/notification.service';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {
  constructor(
    private requestInProgressService: RequestInProgressService,
    private tokenExtractor: HttpXsrfTokenExtractor,
    private notificationService: NotificationService
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let req = request.clone({ setHeaders: { 'Content-Type': 'application/hal+json' } });
    let token = this.tokenExtractor.getToken() as string;
    if (token !== null) {
      req = req.clone({ setHeaders: { 'X-XSRF-TOKEN': token } });
    }
    if (!environment.production) {
      req = req.clone({ headers: req.headers.set('Authorization', 'Basic YWRtaW46YWRtaW4=') });
    }
    return next.handle(req).pipe(
      tap(
        () => {},
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          if (
            error.status !== 404 ||
            !(error instanceof HttpErrorResponse) ||
            error.url.indexOf('environment-information.json') === -1
          ) {
            const { key, messageVariables } = error.error.error || {
              key: 'FALLBACK',
              messageVariables: {}
            };
            this.notificationService.showError(key, messageVariables);
          }
        }
      )
    );
  }
}
