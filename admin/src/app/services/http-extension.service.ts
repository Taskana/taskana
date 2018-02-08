import { Injectable } from '@angular/core';
import { Request, XHRBackend, RequestOptions, Response, Http, RequestOptionsArgs, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { PermissionService } from './permission.service';

@Injectable()
export class HttpExtensionService extends Http {
  permissionService: PermissionService;
  constructor(backend: XHRBackend, defaultOptions: RequestOptions, permissionService: PermissionService) {
    super(backend, defaultOptions);
    this.permissionService = permissionService;
  }

  request(url: string | Request, options?: RequestOptionsArgs): Observable<Response> {
    this.permissionService.setPermission(true);
    return super.request(url, options).catch((error: Response) => {
        if ((error.status === 401 || error.status === 403) && (window.location.href.match(/\?/g) || []).length < 2) {
          this.permissionService.setPermission(false);
        }
        return Observable.throw(error);
    });
  }


}