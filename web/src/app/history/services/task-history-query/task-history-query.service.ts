import { Injectable } from '@angular/core';
import { TaskHistoryEventResourceData } from 'app/shared/models/task-history-event-resource';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Sorting, TaskHistoryQuerySortParameter } from 'app/shared/models/sorting';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StartupService } from '../../../shared/services/startup/startup.service';
import { TaskHistoryQueryFilterParameter } from '../../../shared/models/task-history-query-filter-parameter';
import { QueryPagingParameter } from '../../../shared/models/query-paging-parameter';
import { asUrlQueryString } from '../../../shared/util/query-parameters-v2';

@Injectable({
  providedIn: 'root'
})
export class TaskHistoryQueryService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl();
  }

  getTaskHistoryEvents(
    filterParameter?: TaskHistoryQueryFilterParameter,
    sortParameter?: Sorting<TaskHistoryQuerySortParameter>,
    pagingParameter?: QueryPagingParameter
  ): Observable<TaskHistoryEventResourceData> {
    return this.httpClient.get<TaskHistoryEventResourceData>(
      `${this.url}/v1/task-history-event${asUrlQueryString({
        ...filterParameter,
        ...sortParameter,
        ...pagingParameter
      })}`
    );
  }

  private getQueryParameters(
    orderBy: string,
    sortDirection: string,
    taskId: string,
    parentBPI: string,
    BPI: string,
    eventType: string,
    userId: string,
    domain: string,
    workbasketKey: string,
    porCompany: string,
    porSystem: string,
    porInstance: string,
    porType: string,
    porValue: string,
    taskClassificationKey: string,
    taskClassificationCategory: string,
    attachmentClassificationKey: string,
    custom1: string,
    custom2: string,
    custom3: string,
    custom4: string,
    created: string,
    allPages: boolean = false
  ): void {
    const parameters = new QueryParameters();
    parameters.SORTBY = orderBy;
    parameters.SORTDIRECTION = sortDirection;
    parameters.TASK_ID_LIKE = taskId;
    parameters.PARENT_BUSINESS_PROCESS_ID_LIKE = parentBPI;
    parameters.BUSINESS_PROCESS_ID_LIKE = BPI;
    parameters.EVENT_TYPE_LIKE = eventType;
    parameters.USER_ID_LIKE = userId;
    parameters.DOMAIN = domain;
    parameters.WORKBASKETKEYLIKE = workbasketKey;
    parameters.POR_COMPANY_LIKE = porCompany;
    parameters.POR_SYSTEM_LIKE = porSystem;
    parameters.POR_INSTANCE_LIKE = porInstance;
    parameters.POR_TYPE_LIKE = porType;
    parameters.POR_VALUE_LIKE = porValue;
    parameters.TASK_CLASSIFICATION_KEY_LIKE = taskClassificationKey;
    parameters.TASK_CLASSIFICATION_CATEGORY_LIKE = taskClassificationCategory;
    parameters.ATTACHMENT_CLASSIFICATION_KEY_LIKE = attachmentClassificationKey;
    parameters.CUSTOM_1_LIKE = custom1;
    parameters.CUSTOM_2_LIKE = custom2;
    parameters.CUSTOM_3_LIKE = custom3;
    parameters.CUSTOM_4_LIKE = custom4;
    parameters.CREATED = created;

    if (allPages) {
      delete TaskanaQueryParameters.page;
      delete TaskanaQueryParameters.pageSize;
    }
  }
}
