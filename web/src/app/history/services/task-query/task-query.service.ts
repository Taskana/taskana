import { Injectable } from '@angular/core';
import { TaskHistoryEventData } from 'app/shared/models/task-history-event';
import { TaskHistoryEventResourceData } from 'app/shared/models/task-history-event-resource';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Direction } from 'app/shared/models/sorting';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { StartupService } from '../../../shared/services/startup/startup.service';

@Injectable({
  providedIn: 'root'
})
export class TaskQueryService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl();
  }

  queryTask(
    orderBy: string = 'created',
    sortDirection: string = Direction.ASC,
    searchForValues: TaskHistoryEventData,
    allPages: boolean = false
  ): Observable<TaskHistoryEventResourceData> {
    return this.httpClient.get<TaskHistoryEventResourceData>(
      `${this.url}/v1/task-history-event${this.getQueryParameters(
        orderBy,
        sortDirection,
        searchForValues.taskId,
        searchForValues.parentBusinessProcessId,
        searchForValues.businessProcessId,
        searchForValues.eventType,
        searchForValues.userId,
        searchForValues.domain,
        searchForValues.workbasketKey,
        searchForValues.porCompany,
        searchForValues.porSystem,
        searchForValues.porInstance,
        searchForValues.porType,
        searchForValues.porValue,
        searchForValues.taskClassificationKey,
        searchForValues.taskClassificationCategory,
        searchForValues.attachmentClassificationKey,
        searchForValues.custom1,
        searchForValues.custom2,
        searchForValues.custom3,
        searchForValues.custom4,
        searchForValues.created,
        allPages
      )}`
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
  ): string {
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

    return TaskanaQueryParameters.getQueryParameters(parameters);
  }
}
