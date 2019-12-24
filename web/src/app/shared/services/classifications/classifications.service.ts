import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from 'environments/environment';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, mergeMap, tap} from 'rxjs/operators';

import {Classification} from 'app/models/classification';
import {ClassificationDefinition} from 'app/models/classification-definition';

import {ClassificationResource} from 'app/models/classification-resource';
import {ClassificationCategoriesService} from './classification-categories.service';
import {DomainService} from 'app/services/domain/domain.service';
import {TaskanaQueryParameters} from 'app/shared/util/query-parameters';
import {Direction} from 'app/models/sorting';
import {QueryParametersModel} from 'app/models/query-parameters';

@Injectable()
export class ClassificationsService {

  private url = `${environment.taskanaRestUrl}/v1/classifications/`;
  private classificationSelected = new Subject<ClassificationDefinition>();
  private classificationSaved = new Subject<number>();
  private classificationResourcePromise: Promise<ClassificationResource>;
  private lastDomain: string;

  constructor(
    private httpClient: HttpClient,
    private classificationCategoriesService: ClassificationCategoriesService,
    private domainService: DomainService) {
  }

  private static classificationParameters(domain: string): QueryParametersModel {
    const parameters = new QueryParametersModel();
    parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
    parameters.SORTDIRECTION = Direction.ASC;
    parameters.DOMAIN = domain;
    delete TaskanaQueryParameters.page;
    delete TaskanaQueryParameters.pageSize;

    return parameters;
  }

  // GET
  getClassifications(): Observable<Array<Classification>> {
    return this.domainService.getSelectedDomain().pipe(
      mergeMap(domain => {
        return this.getClassificationObservable(this.httpClient.get<ClassificationResource>(
          `${this.url}${TaskanaQueryParameters.getQueryParameters(ClassificationsService.classificationParameters(domain))}`));

      }),
      tap(() => {
        this.domainService.domainChangedComplete();
      })
    )
  }

  // GET
  getClassificationsByDomain(domain: string, forceRefresh = false): Promise<ClassificationResource> {
    if (this.lastDomain !== domain || !this.classificationResourcePromise || forceRefresh) {
      this.lastDomain = domain;
      this.classificationResourcePromise = this.httpClient.get<ClassificationResource>(
        `${this.url}${TaskanaQueryParameters.getQueryParameters(ClassificationsService.classificationParameters(domain))}`).toPromise();
    }
    return this.classificationResourcePromise;
  }

  // GET
  getClassification(id: string): Promise<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${this.url}${id}`)
      .pipe(tap((classification: ClassificationDefinition) => {
        if (classification) {
          this.classificationCategoriesService.selectClassificationType(classification.type);
        }
      })).toPromise();
  }

  // POST
  postClassification(classification: Classification): Observable<Classification> {
    return this.httpClient.post<Classification>(`${this.url}`, classification);
  }

  // PUT
  putClassification(url: string, classification: Classification): Promise<Classification> {
    return this.httpClient.put<Classification>(url, classification).toPromise();
  }

  // DELETE
  deleteClassification(url: string): Observable<string> {
    return this.httpClient.delete<string>(url);
  }

  // #region "Service extras"
  selectClassification(classification?: ClassificationDefinition) {
    this.classificationSelected.next(classification);
  }

  getSelectedClassification(): Observable<ClassificationDefinition> {
    return this.classificationSelected.asObservable();
  }

  triggerClassificationSaved() {
    this.classificationSaved.next(Date.now());
  }

  classificationSavedTriggered(): Observable<number> {
    return this.classificationSaved.asObservable();
  }

  // #endregion

  private getClassificationObservable(classificationRef: Observable<any>): Observable<Array<Classification>> {
    const classificationTypes: Observable<string> = this.classificationCategoriesService.getSelectedClassificationType();
    return combineLatest(
      [classificationRef,
        classificationTypes]
    ).pipe(
      map(
        (classification: any[]) => {
          return classification[0].classifications ? this.buildHierarchy(classification[0].classifications, classification[1]) : [];
        }
      )
    )
  }

  private buildHierarchy(classifications: Array<Classification>, type: string): Array<Classification> {
    const roots = [];
    const children = [];

    for (let index = 0, len = classifications.length; index < len; ++index) {
      const item = classifications[index];
      if (item.type === type) {
        const parent = item.parentId,
          target = !parent ? roots : (children[parent] || (children[parent] = []));

        target.push(item);
      }
    }
    for (let index = 0, len = roots.length; index < len; ++index) {
      this.findChildren(roots[index], children);
    }
    return roots;
  }

  private findChildren(parent: any, children: Array<any>) {
    if (children[parent.classificationId]) {
      parent.children = children[parent.classificationId];
      for (let index = 0, len = parent.children.length; index < len; ++index) {
        this.findChildren(parent.children[index], children);
      }
    }
  }
}
