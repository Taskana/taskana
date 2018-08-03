import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable, Subject, combineLatest } from 'rxjs';
import { mergeMap, tap } from 'rxjs/operators';

import { Classification } from 'app/models/classification';
import { ClassificationDefinition } from 'app/models/classification-definition';

import { ClassificationResource } from 'app/models/classification-resource';
import { ClassificationCategoriesService } from '../classification-categories-service/classification-categories.service';
import { DomainService } from 'app/services/domain/domain.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Direction } from 'app/models/sorting';

@Injectable()
export class ClassificationsService {

  private url = `${environment.taskanaRestUrl}/v1/classifications/`;
  private classificationSelected = new Subject<ClassificationDefinition>();
  private classificationSaved = new Subject<number>();

  constructor(
    private httpClient: HttpClient,
    private classificationCategoriesService: ClassificationCategoriesService,
    private domainService: DomainService) {
  }

  // GET
  getClassifications(sortBy: string = TaskanaQueryParameters.KEY,
    order: string = Direction.ASC,
    name: string = undefined,
    nameLike: string = undefined,
    descLike: string = undefined,
    owner: string = undefined,
    ownerLike: string = undefined,
    type: string = undefined,
    key: string = undefined,
    keyLike: string = undefined,
    requiredPermission: string = undefined,
    allPages: boolean = true): Observable<Array<Classification>> {
    return this.domainService.getSelectedDomain().pipe(
      mergeMap(domain => {
        return this.getClassificationObservable(this.httpClient.get<ClassificationResource>(
          `${environment.taskanaRestUrl}/v1/classifications/${TaskanaQueryParameters.getQueryParameters(
            sortBy, order, name,
            nameLike, descLike, owner, ownerLike, type, key, keyLike, requiredPermission,
            !allPages ? TaskanaQueryParameters.page : undefined, !allPages ? TaskanaQueryParameters.pageSize : undefined, domain)}`));

      }),
      tap(() => { this.domainService.domainChangedComplete(); })
    )
  }


  // GET
  getClassification(id: string): Observable<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${environment.taskanaRestUrl}/v1/classifications/${id}`)
      .pipe(tap((classification: ClassificationDefinition) => {
        if (classification) {
          this.classificationCategoriesService.selectClassificationType(classification.type);
        }
      }));
  }


  // POST
  postClassification(classification: Classification): Observable<Classification> {
    return this.httpClient.post<Classification>(`${this.url}`, classification);
  }

  // PUT
  putClassification(url: string, classification: Classification): Observable<Classification> {
    return this.httpClient.put<Classification>(url, classification);
  }

  // DELETE
  deleteClassification(url: string): Observable<string> {
    return this.httpClient.delete<string>(url);
  }

  // #region "Service extras"
  selectClassification(classification: ClassificationDefinition) {
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

  private getClassificationObservable(classificationRef: Observable<any>): Observable<any> {
    const classificationTypes = this.classificationCategoriesService.getSelectedClassificationType();
    return combineLatest(
      classificationRef,
      classificationTypes,
      (classification: any, classificationType) => {
        if (!classification._embedded) {
          return [];
        }
        return this.buildHierarchy(classification._embedded.classificationSummaryResourceList, classificationType);
      }
    )
  }

  private buildHierarchy(classifications: Array<Classification>, type: string) {
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

