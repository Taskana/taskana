import { Injectable } from '@angular/core';
import { RequestOptions, Headers, Http, Response } from '@angular/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

import { Category } from '../categoriesadministration/category';

@Injectable()
export class CategoryService {
  private categoryServiceUrl = environment.taskanaRestUrl + '/v1/classifications';  // URL to web API
  constructor(private http: Http) { }
  getCategories(): Observable<Category[]> {
    return this.http.get(this.categoryServiceUrl, this.createAuthorizationHeader())
      .map(this.extractData)
      .catch(this.handleError);
  }
  private extractData(res: Response) {
    let body = res.json();
    console.log("Body: ", body);
    return body;
  }
  private handleError(error: Response | any) {
    // In a real world app, you might use a remote logging infrastructure
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }

  private createAuthorizationHeader() {
    let headers: Headers = new Headers();
    headers.append("Authorization", "Basic dXNlcl8xXzE6dXNlcl8xXzE=");

    return new RequestOptions({ headers: headers });
  }
}
