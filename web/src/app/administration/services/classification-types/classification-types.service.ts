import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { environment } from 'environments/environment';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { ReplaySubject } from 'rxjs/ReplaySubject';

@Injectable()
export class ClassificationTypesService {
  private url = environment.taskanaRestUrl + '/v1/classification-types';
  private classificationTypeSelectedValue = 'TASK';
  private classificationTypeSelected = new BehaviorSubject<string>(this.classificationTypeSelectedValue);
  private dataObs$ = new ReplaySubject<Array<string>>(1);

  constructor(private httpClient: HttpClient
  ) { }

  getClassificationTypes(forceRefresh = false): Observable<Array<string>> {

    if (!this.dataObs$.observers.length || forceRefresh) {
      this.httpClient.get<Array<string>>(this.url).subscribe(
        data => this.dataObs$.next(data),
        error => {
          this.dataObs$.error(error);
          this.dataObs$ = new ReplaySubject(1);
        }
      );
    }

    return this.dataObs$;
  };

  selectClassificationType(id: string) {
    this.classificationTypeSelectedValue = id;
    this.classificationTypeSelected.next(id);
  }

  getSelectedClassificationType(): Observable<string> {
    return this.classificationTypeSelected.asObservable();
  }

}
