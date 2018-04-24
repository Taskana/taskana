import { Task } from '../models/task';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/../environments/environment';

@Injectable()
export class TaskService {
  url = environment.taskanaRestUrl + '/v1/tasks';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/hal+json',
      'Authorization': 'Basic YWRtaW46YWRtaW4=',
      'user': 'user_1_1'
    })
  };

  constructor(private httpClient: HttpClient) {
  }

  findTaskWithWorkbaskets(basketKey: string): Observable<Task[]> {
    return this.httpClient.get<Task[]>(this.url + '?workbasketId=' + basketKey, this.httpOptions);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(this.url + '/' + id, this.httpOptions);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + id + '/complete', '', this.httpOptions);
  }

  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + id + '/claim', 'test', this.httpOptions);
  }

  transferTask(taskId: string, workbasketKey: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + taskId
      + '/transfer/' + workbasketKey, '', this.httpOptions);
  }
}
