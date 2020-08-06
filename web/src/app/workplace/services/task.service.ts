import { Task } from 'app/workplace/models/task';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { TaskResource } from 'app/workplace/models/task-resource';
import { Direction } from 'app/shared/models/sorting';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { QueryParameters } from 'app/shared/models/query-parameters';

@Injectable()
export class TaskService {
  url = `${environment.taskanaRestUrl}/v1/tasks`;

  private taskChangedSource = new Subject<Task>();
  taskChangedStream = this.taskChangedSource.asObservable();
  private taskSelectedSource = new Subject<Task>();
  taskSelectedStream = this.taskSelectedSource.asObservable();

  constructor(private httpClient: HttpClient) {}

  publishUpdatedTask(task?: Task) {
    this.taskChangedSource.next(task);
  }

  selectTask(task?: Task) {
    this.taskSelectedSource.next(task);
  }

  getSelectedTask(): Observable<Task> {
    return this.taskSelectedStream;
  }

  findTasksWithWorkbasket(
    basketId: string,
    sortBy: string,
    sortDirection: string,
    nameLike: string,
    ownerLike: string,
    priority: string,
    state: string,
    objRefTypeLike: string,
    objRefValueLike: string,
    allPages: boolean = false
  ): Observable<TaskResource> {
    const url = `${this.url}${TaskanaQueryParameters.getQueryParameters(
      TaskService.accessIdsParameters(
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
      )
    )}`;
    return this.httpClient.get<TaskResource>(url);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(`${this.url}/${id}`);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/complete`, '');
  }

  // currently unused
  /*  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/claim`, 'test');
  } */

  transferTask(taskId: string, workbasketId: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${taskId}/transfer/${workbasketId}`, '');
  }

  updateTask(task: Task): Observable<Task> {
    const taskConv = TaskService.convertTasksDatesToGMT(task);
    return this.httpClient.put<Task>(`${this.url}/${task.taskId}`, taskConv);
  }

  deleteTask(task: Task): Observable<Task> {
    return this.httpClient.delete<Task>(`${this.url}/${task.taskId}`);
  }

  createTask(task: Task): Observable<Task> {
    return this.httpClient.post<Task>(this.url, task);
  }

  private static convertTasksDatesToGMT(task: Task): Task {
    const timeAttributes = ['created', 'claimed', 'completed', 'modified', 'planned', 'due'];
    timeAttributes.forEach((attributeName) => {
      if (task[attributeName]) {
        task[attributeName] = new Date(task[attributeName]).toISOString();
      }
    });
    return task;
  }

  private static accessIdsParameters(
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
  ): QueryParameters {
    const parameters = new QueryParameters();
    parameters.WORKBASKET_ID = basketId;
    parameters.SORTBY = sortBy;
    parameters.SORTDIRECTION = sortDirection;
    parameters.NAMELIKE = nameLike;
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
