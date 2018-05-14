import { Task } from '../models/task';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/../environments/environment';

@Injectable()
export class TaskService {
  url = environment.taskanaRestUrl + '/v1/tasks';

  constructor(private httpClient: HttpClient) {
  }

  findTaskWithWorkbaskets(basketKey: string): Observable<Task[]> {
    return this.httpClient.get<Task[]>(this.url + '?workbasketId=' + basketKey);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(this.url + '/' + id);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + id + '/complete', '');
  }

  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + id + '/claim', 'test');
  }

  transferTask(taskId: string, workbasketKey: string): Observable<Task> {
    return this.httpClient.post<Task>(this.url + '/' + taskId
      + '/transfer/' + workbasketKey, '');
  }
}
