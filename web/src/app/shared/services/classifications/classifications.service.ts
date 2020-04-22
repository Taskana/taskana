import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';
import { Select, Store } from '@ngxs/store';

import { Classification } from 'app/shared/models/classification';
import { ClassificationDefinition } from 'app/shared/models/classification-definition';

import { ClassificationResource } from 'app/shared/models/classification-resource';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Direction } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { SetSelectedClassificationType } from 'app/shared/store/classification-store/classification.actions';

@Injectable()
export class ClassificationsService {
  private url = `${environment.taskanaRestUrl}/v1/classifications/`;
  private classificationSelected = new Subject<ClassificationDefinition>();
  private classificationSaved = new Subject<number>();
  private classificationResourcePromise: Promise<ClassificationResource>;
  private lastDomain: string;
  // TODO: this should not be here in the service
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;

  constructor(
    private httpClient: HttpClient,
    private domainService: DomainService,
    private store: Store
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
  getClassifications(): Observable<Array<Classification>> {
    return this.domainService.getSelectedDomain().pipe(
      mergeMap(domain => this.getClassificationObservable(this.httpClient.get<ClassificationResource>(
        `${this.url}${TaskanaQueryParameters.getQueryParameters(ClassificationsService.classificationParameters(domain))}`
      ))),
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
  getClassification(id: string): Promise<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${this.url}${id}`)
      .pipe(tap((classification: ClassificationDefinition) => {
        if (classification) {
          this.store.dispatch(new SetSelectedClassificationType(classification.type));
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

  private getClassificationObservable(classificationRef: Observable<ClassificationResource>): Observable<Array<Classification>> {
    return combineLatest(
      [classificationRef,
        this.classificationTypeSelected$]
    ).pipe(
      map(
        ([resource, type]: [ClassificationResource, string]) => (
          resource.classifications ? this.buildHierarchy(resource.classifications, type) : []
        )
      )
    );
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
