import {Task} from 'app/workplace/models/task';
import {Observable, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from 'environments/environment';
import {TaskResource} from 'app/workplace/models/task-resource';
import {Direction} from 'app/models/sorting';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { map } from 'rxjs/operators';

@Injectable()
export class TaskService {

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
  /**
   * @param  {string} basketId the id of workbasket
   * @param {string} sortBy name of field, that the tasks should be sorted by, default is priority
   * @param {string} sortDirection ASC or DESC
   * @param {string} nameLike the name of the task
   * @param {string} ownerLike the owner of the task
   * @param {string} priority the priority of the task
   * @param {string} state the state of the task
   */
  findTasksWithWorkbasket(basketId: string,
                          sortBy = 'priority',
                          sortDirection: string = Direction.ASC,
                          nameLike: string,
                          ownerLike: string,
                          priority: string,
                          state: string,
                          objRefTypeLike: string,
                          objRefValueLike: string,
                          allPages: boolean = false): Observable<TaskResource> {
    const url = `${this.url}${TaskanaQueryParameters.getQueryParameters(
      sortBy, sortDirection, undefined, nameLike, undefined, undefined, ownerLike, undefined, undefined, undefined, undefined,
      !allPages ? TaskanaQueryParameters.page : undefined, !allPages ? TaskanaQueryParameters.pageSize : undefined,
      undefined, undefined, undefined, undefined, basketId, priority, state, objRefTypeLike, objRefValueLike)}`;
    return this.httpClient.get<TaskResource>(url);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(`${this.url}/${id}`)
    .pipe(map(
      (response: Task) => {
        response = this.applyTaskDatesTimeZone(response);
        return response;
    }));
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

  private applyTaskDatesTimeZone(task: Task): Task {
    if (task.due) { task.due = TaskanaDate.applyTimeZone(task.due); }
    if (task.modified) { task.modified = TaskanaDate.applyTimeZone(task.modified); }
    if (task.completed) { task.completed = TaskanaDate.applyTimeZone(task.completed); }
    if (task.planned) { task.planned = TaskanaDate.applyTimeZone(task.planned); }
    if (task.claimed) { task.claimed = TaskanaDate.applyTimeZone(task.claimed); }
    if (task.created) { task.created = TaskanaDate.applyTimeZone(task.created); }
    return task;
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
}
