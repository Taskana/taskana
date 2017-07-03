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
    return this.http.get(environment.taskanaRestUrl + "/v1/workbaskets?requiredPermission=OPEN", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  findTaskWithWorkbaskets(basketName: string): Observable<Task[]> {
    return this.http.get(environment.taskanaRestUrl + "/v1/tasks?workbasketid=" + basketName + "&state=READY&state=CLAIMED", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  getTask(id: string): Observable<Task> {
    return this.http.get(environment.taskanaRestUrl + "/v1/tasks/" + id, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  completeTask(id: string): Observable<Task> {
    return this.http.post(environment.taskanaRestUrl + "/v1/tasks/" + id + "/complete", "", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  claimTask(id: string): Observable<Task> {
    return this.http.post(environment.taskanaRestUrl + "/v1/tasks/" + id + "/claim", "test", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  transferTask(taskId: string, workbasketId: string) {
    return this.http.post(environment.taskanaRestUrl + "/v1/tasks/" + taskId + "/transfer/" + workbasketId, "", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  private createAuthorizationHeader() {
    let headers: Headers = new Headers();
    headers.append("Authorization", "Basic TWF4OnRlc3Q=");

    return new RequestOptions({ headers: headers });
  }
}
