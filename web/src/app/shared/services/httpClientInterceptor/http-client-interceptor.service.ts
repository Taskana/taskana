import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ErrorModel } from 'app/models/modal-error';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { environment } from 'environments/environment';
import { tap } from 'rxjs/operators';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {

	constructor(
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService) {

	}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		req = req.clone({ headers: req.headers.set('Content-Type', 'application/hal+json') });
		if (!environment.production) {
			req = req.clone({ headers: req.headers.set('Authorization', 'Basic YWRtaW46YWRtaW4=') });
		}
		return next.handle(req).pipe(tap(() => { }, error => {
			this.requestInProgressService.setRequestInProgress(false);
			if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
				this.errorModalService.triggerError(
					new ErrorModel('You have no access to this resource ', error));
			} else if (error instanceof HttpErrorResponse && (error.status === 404) && error.url.indexOf('environment-information.json')) {
				// ignore this error message
			} else {
				this.errorModalService.triggerError(
					new ErrorModel('There was error, please contact with your administrator ', error))
			}
		}))
	}
}
