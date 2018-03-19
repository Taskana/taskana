import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { PermissionService } from './permission.service';

@Injectable()
export class HttpClientInterceptor implements HttpInterceptor {
	permissionService: PermissionService;

	constructor(permissionService: PermissionService) {
		this.permissionService = permissionService;
	}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(req).do(event => {
			this.permissionService.setPermission(true);

		}, err => {
			if (err instanceof HttpErrorResponse && (err.status === 401 || err.status === 403)) {
				this.permissionService.setPermission(false)
			}
		});
	}
}
