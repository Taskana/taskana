import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { ErrorModel } from 'app/models/modal-error';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { environment } from 'environments/environment';

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
		return next.handle(req).do(event => {

		}, err => {
			this.requestInProgressService.setRequestInProgress(false);
			if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403)) {
				this.errorModalService.triggerError(
					new ErrorModel('You have no access to this resource ', err));
			} else if (err instanceof HttpErrorResponse && (err.status === 404) && err.url.indexOf('environment-information.json')) {
				// ignore this error message
			} else {
				this.errorModalService.triggerError(
					new ErrorModel('There was error, please contact with your administrator ', err))
			}
		});
	}
}
