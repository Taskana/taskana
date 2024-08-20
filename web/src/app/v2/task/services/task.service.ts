import { Task } from '@task/models/task';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Sorting, TaskQuerySortParameter } from 'app/shared/models/sorting';
import { StartupService } from 'app/shared/services/startup/startup.service'; /* @TODO Legacy */
import { asUrlQueryString } from 'app/shared/util/query-parameters-v2'; /* @TODO Legacy */
import { TaskQueryFilterParameter } from 'app/shared/models/task-query-filter-parameter'; /* @TODO Legacy */
import { QueryPagingParameter } from 'app/shared/models/query-paging-parameter'; /* @TODO Legacy */
import { PagedTaskSummary } from '@task/models/paged-task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getKadaiRestUrl() + '/v1/tasks';
  }

  createTask(task: Task): Observable<Task> {
    return this.httpClient.post<Task>(this.url, task);
  }

  getTasks(
    filterParameter?: TaskQueryFilterParameter,
    sortParameter?: Sorting<TaskQuerySortParameter>,
    pagingParameter?: QueryPagingParameter
  ): Observable<PagedTaskSummary> {
    const url: string = `${this.url}${asUrlQueryString({ ...filterParameter, ...sortParameter, ...pagingParameter })}`;

    return this.httpClient.get<PagedTaskSummary>(url);
  }

  getTask(id: string): Observable<Task> {
    return this.httpClient.get<Task>(`${this.url}/${id}`);
  }

  updateTask(task: Task): Observable<Task> {
    return this.httpClient.put<Task>(`${this.url}/${task.taskId}`, task);
  }

  completeTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/complete`, '');
  }

  claimTask(id: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${id}/claim`, 'test');
  }

  cancelClaimTask(id: string): Observable<Task> {
    return this.httpClient.delete<Task>(`${this.url}/${id}/claim`);
  }

  transferTask(taskId: string, workbasketId: string): Observable<Task> {
    return this.httpClient.post<Task>(`${this.url}/${taskId}/transfer/${workbasketId}`, '');
  }

  deleteTask(task: Task): Observable<Task> {
    return this.httpClient.delete<Task>(`${this.url}/${task.taskId}`);
  }
}
