import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class ClassificationCategoriesService {

  private url = environment.taskanaRestUrl + '/v1/classification-categories';
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient) { }

  getCategories(): Observable<Array<string>> {
    return this.httpClient.get<Array<string>>(this.url, this.httpOptions);
  };
}
