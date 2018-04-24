import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
@Injectable()
export class ClassificationCategoriesService {

  private url = environment.taskanaRestUrl + '/v1/classification-categories';
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };
  private dataObs$ = new ReplaySubject<Array<string>>(1);

  constructor(private httpClient: HttpClient) { }

  getCategories(forceRefresh = false): Observable<Array<string>> {
    if (!this.dataObs$.observers.length || forceRefresh) {
      this.httpClient.get<Array<string>>(this.url, this.httpOptions).subscribe(
        data => this.dataObs$.next(data),
        error => {
          this.dataObs$.error(error);
          this.dataObs$ = new ReplaySubject(1);
        }
      );
    }

    return this.dataObs$;
  };
}
