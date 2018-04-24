import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Workbasket } from 'app/models/workbasket';
import { Task } from '../models/task';
import { environment } from 'app/../environments/environment';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class RestConnectorService {

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient) { }

  getAllWorkBaskets(): Observable<Workbasket[]> {
    return this.httpClient.get<Array<Workbasket>>(environment.taskanaRestUrl + '/v1/workbaskets?requiredPermission=OPEN',
      this.httpOptions);
  }

  findTaskWithWorkbaskets(basketKey: string): Observable<Task[]> {
    return this.httpClient.get<Array<Task>>(environment.taskanaRestUrl + '/v1/tasks?workbasketkey='
      + basketKey + '&state=READY&state=CLAIMED', this.httpOptions);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(environment.taskanaRestUrl + '/v1/tasks/' + id, this.httpOptions);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(environment.taskanaRestUrl + '/v1/tasks/' + id + '/complete', '', this.httpOptions);
  }

  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(environment.taskanaRestUrl + '/v1/tasks/' + id + '/claim', 'test', this.httpOptions);
  }

  transferTask(taskId: string, workbasketKey: string): Observable<any> {
    return this.httpClient.post(environment.taskanaRestUrl + '/v1/tasks/' + taskId
      + '/transfer/' + workbasketKey, '', this.httpOptions);
  }

}
