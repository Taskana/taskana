import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { ErrorModel } from 'app/models/modal-error';

import { PermissionService } from 'app/services/permission/permission.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {

	constructor(
		private permissionService: PermissionService,
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService) {

	}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(req).do(event => {
			this.permissionService.setPermission(true);

		}, err => {
			this.requestInProgressService.setRequestInProgress(false);
			if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403)) {
				this.permissionService.setPermission(false)
			} else if (err instanceof HttpErrorResponse && (err.status === 404) && err.url.indexOf('environment-information.json')) {
				// ignore this error message
			} else {
				this.errorModalService.triggerError(
					new ErrorModel('There was error, please contact with your administrator ', err))
			}
		});
	}
}
