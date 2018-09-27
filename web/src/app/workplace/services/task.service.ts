import {Task} from 'app/workplace/models/task';
import {Observable, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from 'environments/environment';
import {TaskResource} from 'app/workplace/models/task-resource';
import {Direction} from 'app/models/sorting';
import {Workbasket} from 'app/models/workbasket';

@Injectable()
export class TaskService {
  WORKBASKET_ID = 'workbasket-id';
  SORT_BY = 'sortBy';
  SORT_DIRECTION = 'order';
  NAME = 'name';
  OWNER = 'owner';
  PRIORITY = 'priority';
  STATE = 'state';

  url = `${environment.taskanaRestUrl}/v1/tasks`;

  taskChangedSource = new Subject<Task>();
  taskChangedStream = this.taskChangedSource.asObservable();
  taskDeletedSource = new Subject<Task>();
  taskDeletedStream = this.taskDeletedSource.asObservable();
  taskAddedSource = new Subject<Task>();
  taskAddedStream = this.taskAddedSource.asObservable();
  taskSelectedSource = new Subject<Task>();
  taskSelectedStream = this.taskSelectedSource.asObservable();

  constructor(private httpClient: HttpClient) {
  }

  publishUpdatedTask(task: Task) {
    this.taskChangedSource.next(task);
  }

  publishDeletedTask(task: Task) {
    this.taskDeletedSource.next(task);
  }

  publishAddedTask(task: Task) {
    this.taskAddedSource.next(task);
  }

  selectTask(task: Task) {
    this.taskSelectedSource.next(task);
  }

  getSelectedTask(): Observable<Task> {
    return this.taskSelectedStream;
  }

  /**
   * @param {string} basketId
   * @param {string} sortBy name of field, that the tasks should be sorted by, default is priority
   * @returns {Observable<TaskResource>}
   */
  findTasksWithWorkbasket(basketId: string,
                          sortBy = 'priority',
                          sortDirection: string = Direction.ASC,
                          name: string,
                          owner: string,
                          priority: string,
                          state: string): Observable<TaskResource> {
    const url = `${this.url}${this.getTaskQueryParameters(basketId, sortBy, sortDirection, name, owner, priority, state)}`;
    return this.httpClient.get<TaskResource>(url);
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

  createTask(task: Task): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}`, task);
  }

  private getTaskQueryParameters(basketId: string,
                                 sortBy: string,
                                 sortDirection: string,
                                 name: string,
                                 owner: string,
                                 priority: string,
                                 state: string): string {
    let query = '?';
    query += basketId ? `${this.WORKBASKET_ID}=${basketId}&` : '';
    query += `${this.SORT_BY}=${sortBy}&`;
    query += `${this.SORT_DIRECTION}=${sortDirection}&`;
    query += name ? `${this.NAME}=${name}&` : '';
    query += owner ? `${this.OWNER}=${owner}&` : '';
    query += priority ? `${this.PRIORITY}=${priority}&` : '';
    query += state ? `${this.STATE}=${state}&` : '';

    if (query.lastIndexOf('&') === query.length - 1) {
      query = query.slice(0, query.lastIndexOf('&'))
    }
    return query;
  }
}
