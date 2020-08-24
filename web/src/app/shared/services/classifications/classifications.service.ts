import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { mergeMap, tap } from 'rxjs/operators';

import { Classification } from 'app/shared/models/classification';

import { ClassificationPagingList } from 'app/shared/models/classification-paging-list';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Direction } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { StartupService } from '../startup/startup.service';

@Injectable()
export class ClassificationsService {
  private classificationResourcePromise: Promise<ClassificationPagingList>;
  private lastDomain: string;

  constructor(
    private httpClient: HttpClient,
    private domainService: DomainService,
    private startupService: StartupService
  ) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/classifications/';
  }

  private static classificationParameters(domain: string, type?: string): QueryParameters {
    const parameters = new QueryParameters();
    parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
    parameters.SORTDIRECTION = Direction.ASC;
    parameters.DOMAIN = domain;
    parameters.TYPE = type;
    delete TaskanaQueryParameters.page;
    delete TaskanaQueryParameters.pageSize;

    return parameters;
  }

  // GET
  getClassifications(classificationType?: string): Observable<ClassificationPagingList> {
    return this.domainService.getSelectedDomain().pipe(
      mergeMap((domain) =>
        this.httpClient.get<ClassificationPagingList>(
          `${this.url}${TaskanaQueryParameters.getQueryParameters(
            ClassificationsService.classificationParameters(domain, classificationType)
          )}`
        )
      ),
      tap(() => this.domainService.domainChangedComplete())
    );
  }

  // GET
  getClassificationsByDomain(domain: string, forceRefresh = false): Promise<ClassificationPagingList> {
    if (this.lastDomain !== domain || !this.classificationResourcePromise || forceRefresh) {
      this.lastDomain = domain;
      this.classificationResourcePromise = this.httpClient
        .get<ClassificationPagingList>(
          `${this.url}${TaskanaQueryParameters.getQueryParameters(
            ClassificationsService.classificationParameters(domain)
          )}`
        )
        .toPromise();
    }
    return this.classificationResourcePromise;
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
