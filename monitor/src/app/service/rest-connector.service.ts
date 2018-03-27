import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { State } from '../model/state';
import { WorkbasketCounter } from '../model/workbasket-counter';
import { ReportModel } from "../model/report";

@Injectable()
export class RestConnectorService {

  constructor(private http: Http) {
  }

  getTaskStatistics(): Observable<State[]> {
    return this.http.get(environment.taskanaRestUrl + "/v1/monitor/countByState?states=READY,CLAIMED,COMPLETED", this.createAuthorizationHeader())
      .map(res => res.json());
  }


  getWorkbasketStatistics(): Observable<WorkbasketCounter> {
    return this.http.get(environment.taskanaRestUrl + "/v1/monitor/taskcountByWorkbasketDaysAndState?daysInPast=5&states=READY,CLAIMED,COMPLETED", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  getTaskStatusReport(): Observable<ReportModel> {
    return this.http.get(environment.taskanaRestUrl + "/v1/monitor/taskStatusReport", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  private createAuthorizationHeader() {
    let headers: Headers = new Headers();
    headers.append("Authorization", "Basic dXNlcl8xXzE6dXNlcl8xXzE=");

    return new RequestOptions({ headers: headers });
  }
}
