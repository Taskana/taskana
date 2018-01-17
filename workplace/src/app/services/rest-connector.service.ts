import { Injectable } from '@angular/core';
import { Headers, RequestOptions, Http, Response } from '@angular/http';

import { Workbasket } from '../model/workbasket';
import { Task } from '../model/task';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class RestConnectorService {

  constructor(private http: Http) { }

  getAllWorkBaskets(): Observable<Workbasket[]> {
    return this.http.get(environment.taskanaRestUrl + '/v1/workbaskets?requiredPermission=OPEN', this.createAuthorizationHeader())
      .map(res => res.json());
  }

  findTaskWithWorkbaskets(basketKey: string): Observable<Task[]> {
    return this.http.get(environment.taskanaRestUrl + '/v1/tasks?workbasketkey='
      + basketKey + '&state=READY&state=CLAIMED', this.createAuthorizationHeader())
      .map(res => res.json());
  }

  getTask(id: string): Observable<Task> {
    return this.http.get(environment.taskanaRestUrl + '/v1/tasks/' + id, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  completeTask(id: string): Observable<Task> {
    return this.http.post(environment.taskanaRestUrl + '/v1/tasks/' + id + '/complete', '', this.createAuthorizationHeader())
      .map(res => res.json());
  }

  claimTask(id: string): Observable<Task> {
    return this.http.post(environment.taskanaRestUrl + '/v1/tasks/' + id + '/claim', 'test', this.createAuthorizationHeader())
      .map(res => res.json());
  }

  transferTask(taskId: string, workbasketKey: string) {
    return this.http.post(environment.taskanaRestUrl + '/v1/tasks/' + taskId
      + '/transfer/' + workbasketKey, '', this.createAuthorizationHeader())
      .map(res => res.json());
  }

  private createAuthorizationHeader() {
    const headers: Headers = new Headers();
    headers.append('Authorization', 'Basic dXNlcl8xXzE6dXNlcl8xXzE=');

    return new RequestOptions({ headers: headers });
  }
}
