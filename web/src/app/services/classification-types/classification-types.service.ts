import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { environment } from 'environments/environment';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class ClassificationTypesService {
  private url = environment.taskanaRestUrl + '/v1/classification-types';
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };
  private classificationTypeSelectedValue = 'TASK';
  private classificationTypeSelected = new BehaviorSubject<string>(this.classificationTypeSelectedValue);

  constructor(private httpClient: HttpClient) { }

  getClassificationTypes(): Observable<Array<string>> {
    return this.httpClient.get<Array<string>>(this.url, this.httpOptions);
  };

  selectClassificationType(id: string) {
    this.classificationTypeSelectedValue = id;
    this.classificationTypeSelected.next(id);
  }

  getSelectedClassificationType(): Observable<string> {
    return this.classificationTypeSelected.asObservable();
  }

}
