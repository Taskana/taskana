import {Task} from 'app/workplace/models/task';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from 'app/../environments/environment';
import {TaskResource} from 'app/workplace/models/task-resource';

@Injectable()
export class TaskService {
  url = `${environment.taskanaRestUrl}/v1/tasks`;

  constructor(private httpClient: HttpClient) {
  }

  findTasksWithWorkbasket(basketKey: string): Observable<TaskResource> {
    return this.httpClient.get<TaskResource>(`${this.url}?workbasket-id=${basketKey}`);
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

  transferTask(taskId: string, workbasketKey: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${taskId}/transfer/${workbasketKey}`, '');
  }

  updateTask(task: Task): Observable<Task> {
    return this.httpClient.put<Task>(`${this.url}/${task.taskId}`, task);
  }
}
