import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Classification } from 'app/shared/models/classification';

import { ClassificationPagingList } from 'app/shared/models/classification-paging-list';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { ClassificationQuerySortParameter, Sorting } from 'app/shared/models/sorting';
import { StartupService } from '../startup/startup.service';
import { asUrlQueryString } from '../../util/query-parameters-v2';
import { ClassificationQueryFilterParameters } from '../../models/classification-query-filter-parameters';
import { QueryPagingParameter } from '../../models/query-paging-parameter';

@Injectable()
export class ClassificationsService {
  constructor(
    private httpClient: HttpClient,
    private domainService: DomainService,
    private startupService: StartupService
  ) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/classifications/';
  }

  // GET
  getClassifications(
    filterParameter?: ClassificationQueryFilterParameters,
    sortParameter?: Sorting<ClassificationQuerySortParameter>,
    pagingParameter?: QueryPagingParameter
  ): Observable<ClassificationPagingList> {
    return this.httpClient.get<ClassificationPagingList>(
      `${this.url}${asUrlQueryString({ ...filterParameter, ...sortParameter, ...pagingParameter })}`
    );
  }

  // GET
  getClassification(id: string): Observable<Classification> {
    return this.httpClient.get<Classification>(`${this.url}${id}`);
  }

  // POST
  postClassification(classification: Classification): Observable<Classification> {
    return this.httpClient.post<Classification>(`${this.url}`, classification);
  }

  // PUT
  putClassification(classification: Classification): Observable<Classification> {
    return this.httpClient.put<Classification>(`${this.url}${classification.classificationId}`, classification);
  }

  // DELETE
  deleteClassification(id: string): Observable<string> {
    return this.httpClient.delete<string>(`${this.url}${id}`);
  }
}
