import {Task} from 'app/workplace/models/task';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from 'app/../environments/environment';
import {TaskResource} from 'app/workplace/models/task-resource';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class TaskService {
  url = `${environment.taskanaRestUrl}/v1/tasks`;

  taskChangedSource = new Subject<Task>();
  taskChangedStream = this.taskChangedSource.asObservable();

  taskDeletedSource = new Subject<Task>();
  taskDeletedStream = this.taskDeletedSource.asObservable();

  publishUpdatedTask(task: Task) {
    this.taskChangedSource.next(task);
  }

  publishDeletedTask(task: Task) {
    this.taskDeletedSource.next(task);
  }

  constructor(private httpClient: HttpClient) {
  }

  findTasksWithWorkbasket(basketId: string): Observable<TaskResource> {
    return this.httpClient.get<TaskResource>(`${this.url}?workbasket-id=${basketId}`);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(`${this.url}/${id}`);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/complete`, '');
  }

  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/claim`, 'test');
  }

  transferTask(taskId: string, workbasketId: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${taskId}/transfer/${workbasketId}`, '');
  }

  updateTask(task: Task): Observable<Task> {
    return this.httpClient.put<Task>(`${this.url}/${task.taskId}`, task);
  }

  deleteTask(task: Task): Observable<Task> {
    return this.httpClient.delete<Task>(`${this.url}/${task.taskId}`);
  }
}
