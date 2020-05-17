import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable, Subject } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';

import { Classification } from 'app/shared/models/classification';
import { ClassificationDefinition } from 'app/shared/models/classification-definition';

import { ClassificationResource } from 'app/shared/models/classification-resource';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Direction } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';

@Injectable()
export class ClassificationsService {
  private url = `${environment.taskanaRestUrl}/v1/classifications/`;
  private classificationSaved = new Subject<number>();
  private classificationResourcePromise: Promise<ClassificationResource>;
  private lastDomain: string;

  constructor(
    private httpClient: HttpClient,
    private domainService: DomainService,
  ) {}

  private static classificationParameters(domain: string): QueryParameters {
    const parameters = new QueryParameters();
    parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
    parameters.SORTDIRECTION = Direction.ASC;
    parameters.DOMAIN = domain;
    delete TaskanaQueryParameters.page;
    delete TaskanaQueryParameters.pageSize;

    return parameters;
  }

  // GET
  getClassifications(classificationType?: string): Observable<Array<Classification>> {
    return this.domainService.getSelectedDomain().pipe(
      mergeMap(domain => this.getClassificationObservable(this.httpClient.get<ClassificationResource>(
        `${this.url}${TaskanaQueryParameters.getQueryParameters(ClassificationsService.classificationParameters(domain))}`
      ), classificationType)),
      tap(() => {
        this.domainService.domainChangedComplete();
      })
    );
  }

  // GET
  getClassificationsByDomain(domain: string, forceRefresh = false): Promise<ClassificationResource> {
    if (this.lastDomain !== domain || !this.classificationResourcePromise || forceRefresh) {
      this.lastDomain = domain;
      this.classificationResourcePromise = this.httpClient.get<ClassificationResource>(
        `${this.url}${TaskanaQueryParameters.getQueryParameters(ClassificationsService.classificationParameters(domain))}`
      ).toPromise();
    }
    return this.classificationResourcePromise;
  }

  // GET
  getClassification(id: string): Observable<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${this.url}${id}`);
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

  // #region "Service extras"

  triggerClassificationSaved() {
    this.classificationSaved.next(Date.now());
  }

  classificationSavedTriggered(): Observable<number> {
    return this.classificationSaved.asObservable();
  }

  // #endregion

  private getClassificationObservable(
    classificationRef: Observable<ClassificationResource>,
    classificationType: string
  ): Observable<Array<Classification>> {
    return classificationRef.pipe(map(
      (resource: ClassificationResource) => (
        resource.classifications ? this.buildHierarchy(resource.classifications, classificationType) : []
      )
    ));
  }

  private buildHierarchy(classifications: Array<Classification>, type: string): Array<Classification> {
    const roots = [];
    const children = [];

    classifications.forEach(item => {
      if (item.type === type) {
        const parent = item.parentId;
        const target = !parent ? roots : (children[parent] || (children[parent] = []));
        target.push(item);
      }
    });
    roots.forEach(parent => this.findChildren(parent, children));
    return roots;
  }

  private findChildren(parent: any, children: Array<any>) {
    if (children[parent.classificationId]) {
      parent.children = children[parent.classificationId];
      parent.children.forEach(child => this.findChildren(child, children));
    }
  }
}
