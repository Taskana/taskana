import { Task } from 'app/workplace/models/task';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { TaskResource } from 'app/workplace/models/task-resource';
import { Direction } from 'app/models/sorting';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { map } from 'rxjs/operators';
import { QueryParametersModel } from 'app/models/query-parameters';

@Injectable()
export class TaskService {

  url = `${environment.taskanaRestUrl}/v1/tasks`;

  taskChangedSource = new Subject<Task>();
  taskChangedStream = this.taskChangedSource.asObservable();
  taskSelectedSource = new Subject<Task>();
  taskSelectedStream = this.taskSelectedSource.asObservable();

  constructor(private httpClient: HttpClient) {
  }

  publishUpdatedTask(task: Task = new Task('empty')) {
    this.taskChangedSource.next(task);
  }

  selectTask(task?: Task) {
    this.taskSelectedSource.next(task);
  }

  getSelectedTask(): Observable<Task> {
    return this.taskSelectedStream;
  }

  findTasksWithWorkbasket(basketId: string,
    sortBy: string,
    sortDirection: string,
    nameLike: string,
    ownerLike: string,
    priority: string,
    state: string,
    objRefTypeLike: string,
    objRefValueLike: string,
    allPages: boolean = false): Observable<TaskResource> {
    const url = `${this.url}${TaskanaQueryParameters.getQueryParameters(this.accessIdsParameters(
      basketId,
      sortBy,
      sortDirection,
      nameLike,
      ownerLike,
      priority,
      state,
      objRefTypeLike,
      objRefValueLike,
      allPages
    ))}`;
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
    task = this.convertTasksDatesToGMT(task);
    return this.httpClient.put<Task>(`${this.url}/${task.taskId}`, task);
  }

  deleteTask(task: Task): Observable<Task> {
    return this.httpClient.delete<Task>(`${this.url}/${task.taskId}`);
  }

  createTask(task: Task): Observable<Task> {
    return this.httpClient.post<Task>(this.url, task);
  }

  private convertTasksDatesToGMT(task: Task): Task {
    if (task.created) { task.created = new Date(task.created).toISOString(); }
    if (task.claimed) { task.claimed = new Date(task.claimed).toISOString(); }
    if (task.completed) { task.completed = new Date(task.completed).toISOString(); }
    if (task.modified) { task.modified = new Date(task.modified).toISOString(); }
    if (task.planned) { task.planned = new Date(task.planned).toISOString(); }
    if (task.due) { task.due = new Date(task.due).toISOString(); }
    return task;
  }

  private accessIdsParameters(
    basketId: string,
    sortBy = 'priority',
    sortDirection: string = Direction.ASC,
    nameLike: string,
    ownerLike: string,
    priority: string,
    state: string,
    objRefTypeLike: string,
    objRefValueLike: string,
    allPages: boolean = false
  ): QueryParametersModel {

    const parameters = new QueryParametersModel();
    parameters.WORKBASKET_ID = basketId;
    parameters.SORTBY = sortBy;
    parameters.SORTDIRECTION = sortDirection;
    parameters.NAMELIKE = nameLike
    parameters.OWNERLIKE = ownerLike;
    parameters.PRIORITY = priority;
    parameters.STATE = state;
    parameters.TASK_PRIMARY_OBJ_REF_TYPE_LIKE = objRefTypeLike;
    parameters.TASK_PRIMARY_OBJ_REF_VALUE_LIKE = objRefValueLike;
    if (allPages) {
      delete TaskanaQueryParameters.page;
      delete TaskanaQueryParameters.pageSize;
    }

    return parameters;
  }
}
